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

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <div className="navbar-logo">
                    FinlyticZ
                </div>

                {/* Desktop Links */}
                <ul className={`navbar-links ${isMenuOpen ? 'active' : ''}`}>
                    <li><a href="#home" className={activeSection === 'home' ? 'active' : ''} onClick={() => setIsMenuOpen(false)}>Home</a></li>
                    <li><a href="#about" className={activeSection === 'about' ? 'active' : ''} onClick={() => setIsMenuOpen(false)}>About Us</a></li>
                    <li><a href="#features" className={activeSection === 'features' ? 'active' : ''} onClick={() => setIsMenuOpen(false)}>Features</a></li>
                    <li><a href="#pricing" className={activeSection === 'pricing' ? 'active' : ''} onClick={() => setIsMenuOpen(false)}>Pricing Plans</a></li>
                    <li><a href="#" onClick={(e) => { e.preventDefault(); setIsMenuOpen(false); navigate('/partnerwithus'); }}>Partner With Us</a></li>
                    <li><a href="#contact" className={activeSection === 'contact' ? 'active' : ''} onClick={() => setIsMenuOpen(false)}>Contact Us</a></li>

                    {/* Mobile Actions (Visible only in mobile menu) */}
                    <div className="mobile-actions">
                        <Button variant="outline" onClick={() => navigate('/signin')}>Login</Button>
                        <Button variant="primary" icon="bi-download">Desktop App</Button>
                    </div>
                </ul>

                <div className="navbar-actions desktop-only">
                    <Button variant="outline" onClick={() => navigate('/signin')}>Login</Button>
                    <Button variant="primary" icon="bi-download">Desktop App</Button>
                </div>

                {/* Hamburger Menu Icon */}
                <div className="hamburger" onClick={toggleMenu}>
                    <i className={`bi ${isMenuOpen ? 'bi-x-lg' : 'bi-list'}`}></i>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
