import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Navbar.css';
import Button from '../button/Button';

const Navbar = () => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [activeSection, setActiveSection] = useState('home');
    const navigate = useNavigate();

    const toggleMenu = () => {
        setIsMenuOpen(!isMenuOpen);
    };

    useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth > 768) {
                setIsMenuOpen(false);
            }
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    useEffect(() => {
        const sections = ['home', 'about', 'features', 'pricing', 'contact'];
        const sectionElements = sections.map(id => document.getElementById(id)).filter(Boolean);

        const observerOptions = {
            root: document.querySelector('.homepage-container') || null,
            rootMargin: '-20% 0px -70% 0px',
            threshold: 0
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    setActiveSection(entry.target.id);
                }
            });
        }, observerOptions);

        sectionElements.forEach((el) => observer.observe(el));

        return () => observer.disconnect();
    }, []);

    const menuItems = [
        { id: 'home', label: 'Home', icon: 'bi-house-door', path: '#home' },
        { id: 'about', label: 'About Us', icon: 'bi-person', path: '#about' },
        { id: 'features', label: 'Features', icon: 'bi-grid', path: '#features' },
        { id: 'pricing', label: 'Pricing Plans', icon: 'bi-stack', path: '#pricing' },
        { id: 'partner', label: 'Partner With Us', icon: 'bi-people', path: '#', isAction: true, navigateTo: '/partnerwithus' },
        { id: 'contact', label: 'Contact Us', icon: 'bi-chat-left-text', path: '#contact' },
    ];

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <div className="navbar-logo">
                    FinlyticZ
                </div>

                {/* Hamburger Menu Icon (Desktop Hidden, Mobile Visible) */}
                {!isMenuOpen && (
                    <div className="hamburger" onClick={toggleMenu}>
                        <i className="bi bi-list"></i>
                    </div>
                )}

                {/* Mobile/Desktop Menu */}
                <ul className={`navbar-links ${isMenuOpen ? 'active' : ''}`}>
                    {/* Header in Mobile Menu (Visible only when open on mobile) */}
                    {isMenuOpen && (
                        <div className="mobile-menu-header">
                            <div className="navbar-logo">FinlyticZ</div>
                            <div className="menu-close" onClick={toggleMenu}>
                                <i className="bi bi-x"></i>
                            </div>
                        </div>
                    )}

                    {menuItems.map((item) => (
                        <li key={item.id} className={activeSection === item.id ? 'active' : ''}>
                            <a
                                href={item.path}
                                className={activeSection === item.id ? 'active' : ''}
                                onClick={(e) => {
                                    if (item.isAction) {
                                        e.preventDefault();
                                        navigate(item.navigateTo);
                                    }
                                    setIsMenuOpen(false);
                                }}
                            >
                                <span className="menu-icon-box">
                                    <i className={`bi ${item.icon}`}></i>
                                </span>
                                <span className="menu-label">{item.label}</span>
                            </a>
                        </li>
                    ))}

                    {/* Mobile Actions */}
                    <div className="mobile-actions">
                        <button className="btn-mobile-login" onClick={() => navigate('/signin')}>Login</button>
                        <button className="btn-mobile-app" onClick={() => navigate('/download')}>
                            <i className="bi bi-download"></i> Desktop App
                        </button>
                    </div>
                </ul>

                <div className="navbar-actions desktop-only">
                    <Button variant="outline" onClick={() => navigate('/signin')}>Login</Button>
                    <Button variant="primary" icon="bi-download">Desktop App</Button>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
