import React from 'react';
import './Feature.css';

const features = [
    {
        icon: 'bi-receipt-cutoff',
        title: 'Live Tally Data',
        desc: 'Access your Tally data in real time from anywhere and stay updated with every business transaction.',
    },
    {
        icon: 'bi-graph-up-arrow',
        title: 'Inventory Control',
        desc: 'Manage stock efficiently by tracking inventory levels, product movement, and item availability with ease.',
    },
    {
        icon: 'bi-shield-lock-fill',
        title: 'Outstanding Tracking',
        desc: 'Keep a clear record of pending payments, receivables, and payables for better financial control.',
    },
    {
        icon: 'bi-boxes',
        title: 'Business Reports',
        desc: 'Generate meaningful business reports to analyze performance and make informed decisions with confidence.',
    },
    {
        icon: 'bi-people-fill',
        title: 'Multi User Access',
        desc: 'Allow multiple users to access the system securely with role-based permissions and better collaboration.',
    },
    {
        icon: 'bi-cloud-check-fill',
        title: 'Cloud Backup',
        desc: 'Secure your important business data with reliable cloud backup and easy recovery whenever needed.',
    },
    {
        icon: 'bi-bag-check-fill',
        title: 'Order Booking',
        desc: 'Simplify order management by creating, tracking, and processing orders in an organized way.',
    },
    {
        icon: 'bi-calculator-fill',
        title: 'Accounting Made Easy',
        desc: 'Make daily accounting simple and hassle-free with user-friendly features and smooth record management.',
    },
    {
        icon: 'bi-cash-stack',
        title: 'Cash Flow Insights',
        desc: 'Monitor cash inflow and outflow to understand your financial position and plan better for growth.',
    },
    {
        icon: 'bi-journal-text',
        title: 'Registers',
        desc: 'Access all essential Tally registers like sales, purchase, cash, and bank records in one place.',
    },
];

const Features = () => {
    return (
        <section className="features-section" id="features">

            {/* Header */}
            <div className="features-header">
                <h1 className="features-main-title">Features &amp; Benefits</h1>
                <p className="features-main-sub">
                    FinlyticZ packs powerful financial tools into a beautifully simple interface,
                    so you can focus on growing your business, not managing spreadsheets.
                </p>
            </div>

            {/* Feature Cards Grid */}
            <div className="features-grid">
                {features.map((f, i) => (
                    <div className="feature-card" key={i}>
                        <div className="feature-card-header">
                            <div className="feature-icon">
                                <i className={`bi ${f.icon}`}></i>
                            </div>
                            <h3>{f.title}</h3>
                        </div>
                        <p>{f.desc}</p>
                    </div>
                ))}
            </div>

            {/* Bottom CTA */}
            <div className="features-cta-row">
                <a href="#pricing" className="features-cta-btn">
                    See Pricing Plans <i className="bi bi-arrow-right"></i>
                </a>
                <a href="#contact" className="features-cta-outline">
                    Talk to us <i className="bi bi-chat-dots"></i>
                </a>
            </div>

        </section>
    );
};

export default Features;
