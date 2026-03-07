import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../privacypolicy/PrivacyPolicy.css';

const TermsConditionsPage = () => {
    const navigate = useNavigate();

    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);

    const handleBack = () => {
        navigate('/');
    };

    return (
        <div className="pp-container">

            <button className="pp-back-btn" onClick={handleBack}>
                <i className="bi bi-arrow-left"></i> Back to Home
            </button>

            <header className="pp-header">
                <div className="pp-app-name">FinlyticZ</div>
                <div className="pp-company-name">by PayVance</div>
            </header>

            <main className="pp-main">
                <div className="pp-card">
                    <article className="pp-prose">

                        <h1>Terms and Conditions</h1>
                        <p className="pp-updated">Last updated: March 5, 2026</p>

                        <p><strong>Operated by PayVance Innovation Private Limited</strong></p>

                        <hr />

                        <h2>1. Introduction</h2>
                        <p>
                            These Terms and Conditions (“Terms”) govern the access and use of the
                            Finlyticz website, mobile application, and related services (“Platform”)
                            operated by PayVance Innovation Private Limited, having its registered
                            office at A112, Centrum Business Square, Road No. 16, Wagle Estate,
                            Thane, Maharashtra – 400604, India.
                        </p>

                        <p>
                            By accessing or using the Platform, you agree to be bound by these Terms.
                            If you do not agree with any part of these Terms, you must discontinue use
                            of the Platform immediately.
                        </p>

                        <hr />

                        <h2>2. Eligibility</h2>
                        <p>
                            Users must be at least 18 years of age and legally capable of entering
                            into binding agreements under applicable law in order to use the Platform.
                        </p>

                        <hr />

                        <h2>3. User Account</h2>
                        <p>To access certain features of the Platform, users may be required to create an account. You agree to:</p>

                        <ul>
                            <li>Provide accurate and complete information</li>
                            <li>Maintain confidentiality of login credentials</li>
                            <li>Accept responsibility for all activities under your account</li>
                        </ul>

                        <p>
                            The Company reserves the right to suspend or terminate accounts that violate these Terms.
                        </p>

                        <hr />

                        <h2>4. Services</h2>
                        <p>
                            Finlyticz provides analytics, reporting tools, and related digital services
                            intended to assist businesses in understanding financial and operational data.
                        </p>

                        <p>
                            The Company reserves the right to modify, suspend, or discontinue any part
                            of the Platform at any time without prior notice.
                        </p>

                        <hr />

                        <h2>5. Payments</h2>
                        <p>
                            Certain services may require payment through the Website. Payments are
                            processed through secure third-party payment gateways.
                        </p>

                        <p>
                            Users agree to pay all applicable fees associated with the services they
                            purchase. All transactions are subject to the policies of the respective
                            payment processors.
                        </p>

                        <hr />

                        <h2>6. User Obligations</h2>
                        <p>Users agree not to:</p>

                        <ul>
                            <li>Use the Platform for unlawful purposes</li>
                            <li>Attempt unauthorized access to systems or data</li>
                            <li>Distribute malware or harmful software</li>
                            <li>Copy, reproduce, or exploit platform content without authorization</li>
                        </ul>

                        <p>
                            Violation of these obligations may result in account termination and legal action.
                        </p>

                        <hr />

                        <h2>7. Intellectual Property</h2>
                        <p>
                            All content, software, trademarks, branding, and intellectual property
                            associated with the Platform remain the exclusive property of PayVance
                            Innovation Private Limited.
                        </p>

                        <p>
                            Users are granted a limited, non-exclusive, non-transferable license to
                            use the Platform solely for its intended purpose.
                        </p>

                        <hr />

                        <h2>8. Limitation of Liability</h2>
                        <p>
                            To the maximum extent permitted by law, the Company shall not be liable
                            for any indirect, incidental, special, or consequential damages arising
                            from the use or inability to use the Platform.
                        </p>

                        <hr />

                        <h2>9. Termination</h2>
                        <p>
                            The Company reserves the right to suspend or terminate user access to
                            the Platform at its sole discretion if these Terms are violated or if
                            misuse of the Platform is detected.
                        </p>

                        <hr />

                        <h2>10. Third-Party Services</h2>
                        <p>
                            The Platform may integrate with or provide links to third-party services.
                            The Company is not responsible for the practices or policies of such
                            third-party platforms.
                        </p>

                        <hr />

                        <h2>11. Modification of Terms</h2>
                        <p>
                            The Company reserves the right to update or modify these Terms at any
                            time. Continued use of the Platform following changes constitutes
                            acceptance of the revised Terms.
                        </p>

                        <hr />

                        <h2>12. Governing Law</h2>
                        <p>
                            These Terms shall be governed by and interpreted in accordance with the
                            laws of India. Courts located in Thane, Maharashtra shall have exclusive
                            jurisdiction over any disputes arising from these Terms.
                        </p>

                        <hr />

                        <h2>13. Contact Information</h2>

                        <div className="pp-contact-card">
                            <p>For questions or concerns regarding these Terms, please contact:</p>

                            <ul>
                                <li><strong>Company:</strong> PayVance Innovation Private Limited</li>
                                <li>A112, Centrum Business Square</li>
                                <li>Road No. 16, Wagle Estate</li>
                                <li>Thane, Maharashtra – 400604</li>
                                <li>India</li>
                                <li>
                                    <strong>Email:</strong>
                                    <a href="mailto:info@payvance.co.in"> info@payvance.co.in</a>
                                </li>
                            </ul>
                        </div>

                    </article>
                </div>
            </main>
        </div>
    );
};

export default TermsConditionsPage;