import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { planServices, tenantServices } from '../../../services/apiService';
import './PricePlan.css';

const PricePlan = () => {
    const [plans, setPlans] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPlans = async () => {
            try {
                setLoading(true);

                // Fetch both standard plans and trial config in parallel
                const [plansResponse, trialResponse] = await Promise.allSettled([
                    planServices.getAllPlans(),
                    tenantServices.getTrialConfig()
                ]);

                let allMappedPlans = [];

                // 1. Process Trial Plan if available
                if (trialResponse.status === 'fulfilled' && trialResponse.value.data) {
                    const trialConfig = trialResponse.value.data;
                    const totalDays = (trialConfig.adsUnlockedDays || 0) + (trialConfig.extendedTrialDays || 0);

                    allMappedPlans.push({
                        id: 'trial',
                        name: 'Trial Plan',
                        price: '₹0',
                        period: `${totalDays} Days Free`,
                        tag: 'Free Trial',
                        desc: 'Try all premium features with our fully-functional free trial.',
                        features: [
                            `First ${trialConfig.adsUnlockedDays || 0} days without ads`,
                            `Next ${trialConfig.extendedTrialDays || 0} days with ads`,
                            `Up to ${trialConfig.activeUsersCount || 0} users`,
                            `Up to ${trialConfig.companiesCount || 0} company`,
                            'Full Access to All Modules'
                        ],
                        cta: 'Start Free Trial',
                        ctaLink: '/signin',
                        highlighted: false,
                    });
                }

                // 2. Process Standard Plans
                if (plansResponse.status === 'fulfilled') {
                    const mappedPlans = (plansResponse.value.data || []).map((plan, index) => {
                        const priceInfo = plan.plan_price || {};
                        const amount = priceInfo.amount;
                        const period = priceInfo.billing_period === 'monthly' ? '/month' :
                            priceInfo.billing_period === 'yearly' ? '/year' :
                                `/${priceInfo.billing_period || 'month'}`;

                        return {
                            id: plan.id,
                            name: plan.name || `Plan ${plan.id}`,
                            price: amount ? `₹${amount}` : 'Contact Us',
                            period: amount ? period : '',
                            tag: index === 0 ? 'Most Popular' : (plan.tag || 'Standard'),
                            desc: plan.description || 'Flexible plan tailored for your business needs and growth.',
                            features: plan.planFeatures || [
                                'GST Billing & Reports',
                                'Inventory Management',
                                'Financial Analytics',
                                'Mobile App Access',
                                'Secure Data Backup'
                            ],
                            cta: amount ? 'Start Free Trial' : 'Contact Sales',
                            ctaLink: amount ? '/signin' : '#contact',
                            highlighted: index === 0,
                        };
                    });

                    allMappedPlans = [...allMappedPlans, ...mappedPlans];
                } else {
                    console.error('Error fetching plans:', plansResponse.reason);
                    if (allMappedPlans.length === 0) {
                        throw new Error('Failed to load pricing plans');
                    }
                }

                setPlans(allMappedPlans);
                setError(null);
            } catch (err) {
                console.error('Error fetching pricing plans:', err);
                setError('Failed to load pricing plans. Please try again later.');
            } finally {
                setLoading(false);
            }
        };

        fetchPlans();
    }, []);

    const handleCTAClick = (e, link) => {
        if (link.startsWith('/')) {
            e.preventDefault();
            navigate(link);
        } else if (link.startsWith('#')) {
            // Standard anchor behavior for hash links
            return;
        }
    };

    if (loading) {
        return (
            <section className="pricing-section" id="pricing">
                <div className="pricing-header">
                    <h1 className="pricing-main-title">Our Pricing &amp; Plans</h1>
                    <p className="pricing-main-sub">Loading plans...</p>
                </div>
                <div className="pricing-grid">
                    {[1, 2, 3].map((_, i) => (
                        <div key={i} className="pricing-card skeleton">
                            <div className="pricing-card-top">
                                <div className="skeleton-line" style={{ width: '40%' }}></div>
                                <div className="skeleton-line" style={{ width: '60%', height: '2.5rem' }}></div>
                                <div className="skeleton-line" style={{ width: '30%', height: '1.5rem' }}></div>
                                <div className="skeleton-line" style={{ width: '90%', height: '3rem' }}></div>
                            </div>
                            <div className="pricing-features">
                                {[1, 2, 3, 4].map((_, j) => (
                                    <div key={j} className="skeleton-line" style={{ width: '85%' }}></div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            </section>
        );
    }

    if (error) {
        return (
            <section className="pricing-section" id="pricing">
                <div className="pricing-header">
                    <h1 className="pricing-main-title">Our Pricing &amp; Plans</h1>
                    <p className="pricing-main-sub" style={{ color: '#ef4444' }}>{error}</p>
                </div>
            </section>
        );
    }

    return (
        <section className="pricing-section" id="pricing">

            {/* Header */}
            <div className="pricing-header">
                <h1 className="pricing-main-title">Our Pricing &amp; Plans</h1>
                <p className="pricing-main-sub">
                    Discover our transparent and flexible pricing plans designed to cater to your
                    specific needs, ensuring affordability.
                </p>
            </div>

            {/* Cards */}
            <div className="pricing-grid">
                {plans.map((plan, i) => (
                    <div className={`pricing-card ${plan.highlighted ? 'highlighted' : ''}`} key={i}>
                        {plan.highlighted && (
                            <div className="pricing-popular-badge">⭐ Most Popular</div>
                        )}
                        <div className="pricing-card-top">
                            <span className="pricing-plan-tag">{plan.tag}</span>
                            <h3 className="pricing-plan-name">{plan.name}</h3>
                            <div className="pricing-amount">
                                <span className="pricing-price">{plan.price}</span>
                                <span className="pricing-period">{plan.period}</span>
                            </div>
                            <p className="pricing-desc">{plan.desc}</p>
                        </div>
                        <ul className="pricing-features">
                            {plan.features.map((f, j) => (
                                <li key={j}>
                                    <i className="bi bi-check-circle-fill"></i>
                                    {f}
                                </li>
                            ))}
                        </ul>
                        <a
                            href={plan.ctaLink}
                            className={`pricing-cta ${plan.highlighted ? 'primary' : 'outline'}`}
                            onClick={(e) => handleCTAClick(e, plan.ctaLink)}
                        >
                            {plan.cta}
                        </a>
                    </div>
                ))}
            </div>

        </section>
    );
};

export default PricePlan;
