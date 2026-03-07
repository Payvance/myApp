import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './PrivacyPolicy.css';

const PrivacyPolicyPage = () => {
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
                <div className="pp-company-name">by Payvance</div>
            </header>

            <main className="pp-main">
                <div className="pp-card">
                    <article className="pp-prose">

                        <h1>Privacy Policy - FinlyticZ</h1>
                        <p className="pp-updated">Operated by PayVance Innovation Private Limited</p>

                        <hr />

                        <h2>1. Legal Introduction & Acceptance</h2>
                        <p>
                            This Privacy Policy governs the collection, use, disclosure, storage,
                            and protection of information by PayVance Innovation Private Limited,
                            incorporated under the Companies Act, 2013, having its registered office at
                            A112, Centrum Business Square, Road No. 16, Wagle Estate, Thane,
                            Maharashtra – 400604, India.
                        </p>

                        <p>
                            This Policy applies to www.finlyticz.com, the FinlyticZ mobile application,
                            and all related services and digital platforms.
                        </p>

                        <p>
                            By accessing or using the Platform, you provide free, informed,
                            and unconditional consent to the practices described in this Policy.
                            If you do not agree, you must discontinue use immediately.
                        </p>

                        <hr />

                        <h2>2. Information We Collect</h2>

                        <h3>Personal Information</h3>
                        <ul>
                            <li>Name</li>
                            <li>Business name</li>
                            <li>Email address</li>
                            <li>Phone number</li>
                            <li>Designation</li>
                            <li>Login credentials</li>
                        </ul>

                        <h3>Transaction Information (Website Only)</h3>
                        <p>
                            Payments on the Website are processed through secure third-party payment
                            gateways such as Cashfree or other PCI-DSS compliant processors.
                        </p>

                        <ul>
                            <li>Billing details</li>
                            <li>Transaction ID</li>
                            <li>Payment status</li>
                        </ul>

                        <p>
                            <strong>Note:</strong> We do not store debit or credit card details.
                        </p>

                        <h3>Technical & Usage Data</h3>
                        <ul>
                            <li>IP address</li>
                            <li>Device type and operating system</li>
                            <li>Browser information</li>
                            <li>Session logs</li>
                            <li>Crash analytics</li>
                        </ul>

                        <p>
                            We may also use analytics technologies such as Google Analytics or
                            similar tools to understand platform usage and improve services.
                        </p>

                        <hr />

                        <h2>3. Purpose of Data Processing</h2>
                        <p>We process data strictly for:</p>

                        <ul>
                            <li>Account authentication</li>
                            <li>Providing analytics services</li>
                            <li>Processing website payments</li>
                            <li>Customer support</li>
                            <li>Platform improvement</li>
                            <li>Fraud detection and prevention</li>
                            <li>Legal compliance</li>
                        </ul>

                        <p>
                            By registering on the platform, users consent to receive service
                            notifications, account alerts, and transactional communications via email.
                        </p>

                        <hr />

                        <h2>4. Data Sharing</h2>
                        <p>Data may be shared with:</p>

                        <ul>
                            <li>Service providers such as hosting partners, payment gateways, and infrastructure providers</li>
                            <li>Legal authorities where required by law or regulation</li>
                            <li>Corporate entities in the event of merger, restructuring, or acquisition</li>
                        </ul>

                        <p>
                            All third parties operate under strict confidentiality obligations.
                        </p>

                        <hr />

                        <h2>5. Data Security</h2>
                        <p>
                            We implement industry-standard safeguards including:
                        </p>

                        <ul>
                            <li>Secure server infrastructure</li>
                            <li>SSL encrypted communications</li>
                            <li>Restricted internal access controls</li>
                            <li>Monitoring and security procedures</li>
                        </ul>

                        <p>
                            User data may be stored on secure cloud servers maintained by trusted
                            infrastructure providers. However, no digital system is completely
                            secure, and users acknowledge inherent internet risks.
                        </p>

                        <hr />

                        <h2>6. Data Retention</h2>
                        <p>
                            Data is retained only for as long as necessary to provide services,
                            comply with legal requirements, resolve disputes, and prevent fraud.
                            Data may be anonymized or securely deleted when no longer required.
                        </p>

                        <hr />

                        <h2>7. User Rights</h2>

                        <p>Users may request:</p>

                        <ul>
                            <li>Access to stored personal data</li>
                            <li>Correction of inaccurate information</li>
                            <li>Deletion where legally permissible</li>
                            <li>Withdrawal of consent for certain processing</li>
                            <li>Opt-out of marketing communications</li>
                        </ul>

                        <p>
                            Requests can be made by contacting the Grievance Officer below.
                        </p>

                        <hr />

                        <h2>8. Cookies Policy</h2>

                        <p>
                            The Website may use cookies for authentication, session management,
                            and analytics purposes. Users may disable cookies via browser settings,
                            although certain features of the platform may not function properly.
                        </p>

                        <hr />

                        <h2>9. Limitation of Liability</h2>

                        <p>
                            To the maximum extent permitted by law, the Company shall not be liable
                            for indirect, incidental, or consequential damages including those
                            resulting from cyber-attacks, force majeure events, or third-party
                            infrastructure failures beyond reasonable control.
                        </p>

                        <hr />

                        <h2>10. Indemnification</h2>

                        <p>
                            Users agree to indemnify and hold harmless PayVance Innovation Private
                            Limited from any claims, damages, or liabilities arising from misuse
                            of the platform or violation of this Privacy Policy.
                        </p>

                        <hr />

                        <h2>11. Governing Law & Jurisdiction</h2>

                        <p>
                            This Policy shall be governed by the laws of India. Courts located in
                            Thane, Maharashtra shall have exclusive jurisdiction.
                        </p>

                        <hr />

                        <h2>12. Grievance Officer</h2>

                        <div className="pp-contact-card">
                            <ul>
                                <li><strong>Company:</strong> PayVance Innovation Private Limited</li>
                                <li><strong>Address:</strong> A112, Centrum Business Square, Road No. 16, Wagle Estate, Thane, Maharashtra – 400604, India</li>
                                <li><strong>Email:</strong> <a href="mailto:info@payvance.co.in">info@payvance.co.in</a></li>
                            </ul>

                            <p>
                                Grievances will be acknowledged within 48 hours and resolved within
                                30 days.
                            </p>
                        </div>

                    </article>
                </div>
            </main>
        </div>
    );
};

export default PrivacyPolicyPage;