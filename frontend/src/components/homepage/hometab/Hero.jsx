import React from 'react';
import './Hero.css';
import Button from '../button/Button';

const Hero = () => {
    const scrollToSection = (id) => {
        const el = document.getElementById(id);
        if (el) {
            el.scrollIntoView({ behavior: 'smooth' });
        }
    };

    return (
        <section className="hero-section" id="home">
            {/* Badge Pill */}
            <div className="hero-badge">
                <span className="hero-badge-dot"></span>
                Start your free trial today
            </div>

            {/* Main Heading */}
            <h1 className="hero-title">
                Future-Ready Mobile ERP for <br />
                <span className="hero-title-accent">Smarter Business Control</span>
            </h1>

            {/* Subtitle */}
            <p className="hero-subtitle">
                FinlyticZ empowers businesses with automated accounting, seamless Tally integration, and secure cloud backup to keep your financial data organized and accessible.
            </p>

            {/* CTA Buttons */}
            <div className="hero-actions">
                <Button
                    variant="primary"
                    className="btn-explore"
                    onClick={() => scrollToSection('features')}
                >
                    Explore Features
                </Button>
                <Button
                    variant="outline"
                    className="btn-download"
                >
                    Download App
                </Button>
            </div>

            {/* User Manual Link */}
            <div className="hero-manual">
                <a href="#manual">
                    <i className="bi bi-book"></i>
                    How it works — User Manual
                </a>
            </div>

            {/* How it Works — 3 Step Cards */}
            <div className="hero-steps-row">

                <div className="how-card">
                    <span className="how-label">STEP 01</span>
                    <div className="how-icon-wrap">
                        <i className="bi bi-phone"></i>
                        <span className="how-icon-badge"><i className="bi bi-download"></i></span>
                    </div>
                    <h4>Download Mobile App</h4>
                    <p>Sign up for free and get a 7-day trial — no credit card required to get started.</p>
                    <div className="how-pill"><i className="bi bi-check-lg"></i> Free trial included</div>
                </div>

                <div className="how-arrow"><i className="bi bi-arrow-right"></i></div>

                <div className="how-card">
                    <span className="how-label">STEP 02</span>
                    <div className="how-icon-wrap">
                        <i className="bi bi-display"></i>
                        <span className="how-icon-badge"><i className="bi bi-download"></i></span>
                    </div>
                    <h4>Download Desktop App</h4>
                    <p>Setup and configure with Tally ERP/Prime, then securely pair with your mobile app.</p>
                    <div className="how-pill"><i className="bi bi-check-lg"></i> Easy configuration</div>
                </div>

                <div className="how-arrow"><i className="bi bi-arrow-right"></i></div>

                <div className="how-card">
                    <span className="how-label">STEP 03</span>
                    <div className="how-icon-wrap">
                        <i className="bi bi-check2"></i>
                        <span className="how-icon-badge"><i className="bi bi-star-fill"></i></span>
                    </div>
                    <h4>Download completed, Enjoy the App!</h4>
                    <p>Scan the QR code from your mobile app on the Desktop app and you're live instantly.</p>
                    <div className="how-pill"><i className="bi bi-check-lg"></i> Instant activation</div>
                </div>

            </div>

        </section>
    );
};

export default Hero;
