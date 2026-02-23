import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Footer.css';
import CommonFooter from '../../common/footer/Footer';

const quickLinks = [
    { label: 'Home', href: '#home' },
    { label: 'About Us', href: '#about' },
    { label: 'Features', href: '#features' },
    { label: 'Pricing', href: '#pricing' },
    { label: 'Contact Us', href: '#contact' },
];

const supportLinks = [
    { label: 'Terms &amp; Conditions', },
    { label: 'Privacy Policy', },
];

const socialLinks = [
    { icon: 'bi-linkedin', href: 'https://linkedin.com', label: 'LinkedIn' },
    { icon: 'bi-instagram', href: 'https://instagram.com', label: 'Instagram' },
];

const Footer = () => {
    const navigate = useNavigate();

    const scrollTo = (href) => {
        if (href.startsWith('#')) {
            const el = document.querySelector(href);
            if (el) el.scrollIntoView({ behavior: 'smooth' });
        }
    };

    const openPDF = (path) => {
        window.open(path, '_blank', 'noopener,noreferrer');
    };

    return (
        <footer className="ft-footer">

            {/* ── Top Grid ── */}
            <div className="ft-grid">

                {/* 1 — Company Info */}
                <div className="ft-col ft-brand-col">
                    <div className="ft-logo">FinlyticZ</div>
                    <p className="ft-tagline">
                        Empowering businesses with smarter finance — invoicing, GST, analytics,
                        and payments in one beautiful platform.
                    </p>
                    <div className="ft-address">
                        <i className="bi bi-geo-alt-fill"></i>
                        <span>
                            PayVance Innovation Pvt. Ltd.<br />
                            A112 Centrum Business Square, Wagle Estate,<br />
                            Thane , Mumbai-400604<br />
                        </span>
                    </div>
                </div>

                {/* 2 — Quick Links */}
                <div className="ft-col">
                    <h4 className="ft-col-title">Quick Links</h4>
                    <ul className="ft-link-list">
                        {quickLinks.map((l, i) => (
                            <li key={i}>
                                <a
                                    href={l.href}
                                    onClick={(e) => { e.preventDefault(); scrollTo(l.href); }}
                                >
                                    <i className="bi bi-chevron-right"></i> {l.label}
                                </a>
                            </li>
                        ))}
                        <li>
                            <a
                                href="/partnerwithus"
                                onClick={(e) => { e.preventDefault(); navigate('/partnerwithus'); }}
                            >
                                <i className="bi bi-chevron-right"></i> Partner With Us
                            </a>
                        </li>
                    </ul>
                </div>

                {/* 3 — Product & Support */}
                <div className="ft-col">
                    <h4 className="ft-col-title">Product &amp; Support</h4>
                    <ul className="ft-link-list">
                        {supportLinks.map((l, i) => (
                            <li key={i}>
                                <a
                                    href={l.pdf}
                                    onClick={(e) => { e.preventDefault(); openPDF(l.pdf); }}
                                >
                                    <i className="bi bi-file-earmark-text"></i>
                                    <span dangerouslySetInnerHTML={{ __html: l.label }} />
                                </a>
                            </li>
                        ))}
                    </ul>
                </div>

                {/* 4 — Contact Information */}
                <div className="ft-col">
                    <h4 className="ft-col-title">Contact Us</h4>
                    <ul className="ft-contact-list">
                        <li>
                            <i className="bi bi-telephone-fill"></i>
                            <div>
                                <span>+91- 22- 45070723</span>
                            </div>
                        </li>
                        <li>
                            <i className="bi bi-envelope-fill"></i>
                            <div>
                                <span>info@payvance.co.in</span>
                            </div>
                        </li>
                        <li>
                            <i className="bi bi-clock-fill"></i>
                            <div>
                                <span>Mon–Fri: 9 AM – 6 PM</span>
                                <span className="cu-closed">Sat-Sun: Closed</span>
                            </div>
                        </li>
                    </ul>
                </div>

            </div>

            {/* ── Social Media ── */}
            <div className="ft-social-row">
                <p className="ft-follow">Follow us on</p>
                <div className="ft-socials">
                    {socialLinks.map((s, i) => (
                        <a
                            key={i}
                            href={s.href}
                            target="_blank"
                            rel="noopener noreferrer"
                            aria-label={s.label}
                            className="ft-social-icon"
                        >
                            <i className={`bi ${s.icon}`}></i>
                        </a>
                    ))}
                </div>
            </div>

            {/* ── Divider ── */}
            <div className="ft-divider"></div>

            {/* ── Common Footer (Copyright & Version) ── */}
            <CommonFooter />

        </footer>
    );
};

export default Footer;
