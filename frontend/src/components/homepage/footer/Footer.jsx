import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Footer.css';
import CommonFooter from '../../common/footer/Footer';
import { companyConfigServices } from '../../../services/apiService'; // ✅ adjust path if needed

const quickLinks = [
    { label: 'Home', href: '#home' },
    { label: 'About Us', href: '#about' },
    { label: 'Features', href: '#features' },
    { label: 'Pricing', href: '#pricing' },
    { label: 'Contact Us', href: '#contact' },
];

const supportLinks = [
    { label: 'Terms & Conditions' },
    { label: 'Privacy Policy' },
];

const socialLinks = [
    { icon: 'bi-linkedin', href: 'https://www.linkedin.com/company/payvance', label: 'LinkedIn' },
    { icon: 'bi-instagram', href: 'https://www.instagram.com/payvanceinnovations?igsh=eTVtMjg3bTE5NDBt', label: 'Instagram' },
];

const Footer = () => {
    const navigate = useNavigate();

    // ✅ Company details state
    const [companyDetails, setCompanyDetails] = useState({
        companyName: '',
        email: '',
        phone: '',
        address: '',
    });

    // ✅ Fetch company config
    useEffect(() => {
        const fetchCompanyDetails = async () => {
            try {
                const res = await companyConfigServices.getCompanyDetails();

                const configObj = res.data.reduce((acc, item) => {
                    acc[item.code] = item.value;
                    return acc;
                }, {});

                setCompanyDetails({
                    companyName: configObj.companyName,
                    email: configObj.email,
                    phone: configObj.phone,
                    address: configObj.address,
                });

            } catch (error) {
                console.error('Error fetching company details:', error);
            }
        };

        fetchCompanyDetails();
    }, []);

    const scrollTo = (href) => {
        if (href.startsWith('#')) {
            const el = document.querySelector(href);
            if (el) el.scrollIntoView({ behavior: 'smooth' });
        }
    };

    return (
        <footer className="ft-footer">

            {/* ── Top Grid ── */}
            <div className="ft-grid">

                {/* 1 — Company Info */}
                <div className="ft-col ft-brand-col">
                    <div className="ft-logo">
                        {companyDetails.companyName}
                    </div>

                    <p className="ft-tagline">
                        Designed for smarter financial management.
                        Streamlined invoicing and tax compliance.
                        Actionable analytics at your fingertips.
                    </p>

                    <div className="ft-brand-details">
                        <div className="ft-address">
                            <i className="bi bi-geo-alt-fill"></i>
                            <span style={{ whiteSpace: 'pre-line' }}>
                                {companyDetails.address}
                            </span>
                        </div>
                        <div className="ft-map-square">
                            <iframe
                                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3767.8967406240217!2d72.95159107598!3d19.20015568703491!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3be7b913619a970d%3A0x3565e06495db1670!2sCentrum%20Business%20Square!5e0!3m2!1sen!2sin!4v1740304033306!5m2!1sen!2sin"
                                width="100%"
                                height="100%"
                                style={{ border: 0 }}
                                allowFullScreen=""
                                loading="lazy"
                                referrerPolicy="no-referrer-when-downgrade"
                                title="Office Location"
                            ></iframe>
                        </div>
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
                                    onClick={(e) => {
                                        e.preventDefault();
                                        scrollTo(l.href);
                                    }}
                                >
                                    <i className="bi bi-chevron-right"></i> {l.label}
                                </a>
                            </li>
                        ))}
                        <li>
                            <a
                                href="/partnerwithus"
                                onClick={(e) => {
                                    e.preventDefault();
                                    navigate('/partnerwithus');
                                }}
                            >
                                <i className="bi bi-chevron-right"></i> Partner With Us
                            </a>
                        </li>
                    </ul>
                </div>

                {/* 3 — Product & Support */}
                <div className="ft-col">
                    <h4 className="ft-col-title">Product & Support</h4>
                    <ul className="ft-link-list">
                        {supportLinks.map((l, i) => (
                            <li key={i}>
                                <a href="#">
                                    <i className="bi bi-file-earmark-text"></i> {l.label}
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
                                <span>{companyDetails.phone}</span>
                            </div>
                        </li>
                        <li>
                            <i className="bi bi-envelope-fill"></i>
                            <div>
                                <span>{companyDetails.email}</span>
                            </div>
                        </li>
                        <li>
                            <i className="bi bi-clock-fill"></i>
                            <div>
                                <span>24/7 Available</span>
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

            <div className="ft-divider"></div>

            {/* Copyright */}
            <CommonFooter />

        </footer>
    );
};

export default Footer;