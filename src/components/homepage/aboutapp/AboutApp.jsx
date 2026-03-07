import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import packageJson from '../../../../package.json';
import '../privacypolicy/PrivacyPolicy.css';

const AboutAppPage = () => {
    const navigate = useNavigate();

    // Scroll to top on mount
    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);

    const handleBack = () => {
        navigate('/');
    };

    return (
        <div className="pp-container">
            {/* Sticky Back Button for better UX */}
            <button className="pp-back-btn" onClick={handleBack}>
                <i className="bi bi-arrow-left"></i> Back to Home
            </button>

            <header className="pp-header">
                <div className="pp-app-name">FinlyticZ</div>
                <div className="pp-company-name">by Payvance</div>
            </header>

            <main className="pp-main">
                <div className="pp-card">
                    <article className="pp-prose">
                        <h1>About FinlyticZ</h1>
                        <p className="pp-updated">Last updated: March 5, 2026</p>
                        <p className="pp-updated" style={{ background: 'rgba(79, 70, 229, 0.08)', color: '#4f46e5' }}>
                            App version: 1.0.0
                        </p>

                        <p>
                            FinlyticZ is your pocket-friendly business companion for tracking
                            accounts, inventory, and performance on the go.
                        </p>
                        <p>
                            View outstanding receivables and payables, manage items and stock,
                            monitor parties and clients, and explore powerful sales and
                            financial reports in a clean, mobile-first dashboard.
                        </p>
                        <p>
                            Designed for growing businesses, FinlyticZ brings key insights like
                            aging, stock status, and category-wise analytics into one simple app
                            so you can make faster, smarter decisions—anytime, anywhere.
                        </p>

                        <div className="pp-contact-card" style={{ marginTop: '3rem' }}>
                            <p>
                                <strong>Director: Shrinivas Patil </strong>
                            </p>
                            <p style={{ fontSize: '0.9rem', color: '#64748b', marginTop: '1rem' }}>
                                PayVance Innovation Private Limited. Copyright © 2026. All rights reserved.
                            </p>
                        </div>
                    </article>
                </div>
            </main>
        </div>
    );
};

export default AboutAppPage;
