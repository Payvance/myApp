import React from 'react';
import './Feature.css';

const features = [
    {
        icon: 'bi-receipt-cutoff',
        title: 'Easy Billing',
        desc: 'Generate professional invoices in seconds. Customise templates, add discounts, and send to clients with one click.',
    },
    {
        icon: 'bi-graph-up-arrow',
        title: 'Profit / Loss Reports',
        desc: 'Real-time P&L dashboards with drill-down by category, date range, and product. Know exactly where you stand.',
    },
    {
        icon: 'bi-shield-lock-fill',
        title: 'Secure Data',
        desc: 'Bank-grade encryption, role-based access, and daily backups keep your financial data completely protected.',
    },
    {
        icon: 'bi-boxes',
        title: 'Stock & Inventory',
        desc: 'Track stock levels in real time, set low-stock alerts, and manage multiple warehouses from a single dashboard.',
    },
    {
        icon: 'bi-people-fill',
        title: 'Party Management',
        desc: 'Maintain a complete ledger for every customer and vendor — outstanding dues, payment history, and credit limits.',
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
                        <div className="feature-icon">
                            <i className={`bi ${f.icon}`}></i>
                        </div>
                        <h3>{f.title}</h3>
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
