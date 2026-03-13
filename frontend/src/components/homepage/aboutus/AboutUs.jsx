import React from 'react';
import './About.css';

const differentiators = [
    {
        icon: 'bi-award',
        title: 'Expertise',
        desc: 'Our team consists of highly skilled professionals with deep understanding of FinTech systems and financial operations.',
    },
    {
        icon: 'bi-lightbulb',
        title: 'Flexible Solution',
        desc: 'Built to flex for every type of business — from startups to enterprises — without compromising on speed or accuracy.',
    },
    {
        icon: 'bi-currency-dollar',
        title: 'Cost Effectiveness',
        desc: 'Pricing plans designed to offer the most competitive structure that fits businesses of every size and stage.',
    },
    {
        icon: 'bi-headset',
        title: 'Comprehensive Support',
        desc: 'Dedicated support over text and on call — from setup to daily usage, we are always available for you.',
    },
];

const storyMilestones = [
    {
        year: '2014',
        icon: 'bi-rocket-takeoff',
        title: 'Founded',
        desc: 'Sil Technology Private Limited was founded with a mission to democratise financial technology for businesses of all sizes.',
    },
    {
        year: '2024',
        icon: 'bi-code-slash',
        title: 'Acquisition',
        desc: 'Acquired PayVance Innovation Private Limited to expand our product offerings and market reach.',
    },
    {
        year: '2025',
        icon: 'bi-globe2',
        title: 'Scaling Up',
        desc: ' Sil Technology to PayVance Innovation Private Limited',
    },
    {
        year: '2026',
        icon: 'bi-stars',
        title: 'FinlyticZ Born',
        desc: 'FinlyticZ launched as our flagship SaaS product — bringing real-time analytics, smart alerts, and AI automation to every business.',
    },
];

const teamMembers = [
    { initials: 'SP', name: 'Shrinivas Patil', role: 'Director', linkedin: '#', twitter: '#' },
    { initials: 'SS', name: 'Sohel Shaikh', role: 'Key Accounts Manager', linkedin: '#', twitter: '#' },
    { initials: 'AS', name: 'Akash Singh', role: 'Staff Engineer', linkedin: '#', twitter: '#' },
    { initials: 'AD', name: 'Aniket Desai', role: 'Associate Engineering Manager', linkedin: '#', twitter: '#' },
];

const coreValues = [
    {
        icon: 'bi-magic',
        title: 'Simplicity First',
        tagline: '“Clarity drives better decisions.”',
        desc: 'Business intelligence should be simple and easy to understand. We transform complex financial data into clear insights so business owners can quickly understand their numbers and make confident decisions.',
    },
    {
        icon: 'bi-person-heart',
        title: 'Customer-Centric Thinking',
        tagline: '“Build what truly matters.”',
        desc: 'We design every feature with our users in mind. By understanding real business challenges, we focus on solutions that create meaningful value for our customers'
    },
    {
        icon: 'bi-shield-lock-fill',
        title: 'Security First',
        tagline: '“Trust is built on protection.”',
        desc: 'Financial data is highly sensitive, and protecting it is our priority. We ensure strong security, privacy, and reliable systems to keep our users’ data safe.'
    },
    {
        icon: 'bi-graph-up-arrow',
        title: 'Continuous Innovation',
        tagline: '“Always improving.”',
        desc: 'Businesses evolve, and so do we. We continuously enhance our platform to deliver smarter, faster, and more intuitive business analytics.'
    },
    {
        icon: 'bi-patch-check-fill',
        title: 'Data Integrity',
        tagline: '“Accuracy in every decimal.”',
        desc: 'Financial insights must be precise and reliable. We maintain accurate data and consistent reporting to provide a trustworthy source of truth for every business.'
    },
];

const AboutUs = () => {
    return (
        <section className="about-section" id="about">

            <div className="about-header">
                <h1 className="about-main-title">About FinlyticZ</h1>
                <p className="about-main-sub">
                    Empowering businesses to thrive in a digital economy with our dedicated
                    team of professionals and modern FinTech expertise.
                </p>
            </div>

            {/* ── 1. Who We Are — Two Column Intro ── */}
            <div className="about-intro-wrap">
                <div className="about-intro-text">
                    <span className="about-eyebrow">WHO ARE WE</span>
                    <h2>A dedicated team for your digital finance journey</h2>
                    <p>
                        Welcome to FinlyticZ, your ultimate resource for digital finance and payments.
                        We are a dedicated team of professionals passionate about helping businesses harness
                        the power of modern FinTech. With in-depth knowledge, practical experience, and a
                        commitment to excellence, we empower businesses to thrive in a digital economy.
                    </p>
                    <a href="#features" className="about-cta">
                        Explore Features <i className="bi bi-arrow-right"></i>
                    </a>
                </div>

                <div className="about-intro-visual">
                    <div className="about-circle">
                        <div className="about-circle-inner">
                            <span className="about-circle-brand">FinlyticZ</span>
                            <span className="about-circle-tagline">Phone nikalo, business sambhalo</span>
                        </div>
                    </div>
                </div>
            </div>

            {/* ── 2. Our Story — Timeline ── */}
            <div className="about-story-wrap">
                <div className="about-story-header">
                    <span className="about-eyebrow">OUR STORY</span>
                    <h2>From idea to impact — our journey</h2>
                    <p>A timeline of milestones that shaped FinlyticZ into what it is today.</p>
                </div>
                <div className="about-story-timeline">
                    {storyMilestones.map((m, i) => (
                        <div className="story-card" key={i}>
                            <div className="story-year">{m.year}</div>
                            <div className="story-icon-wrap">
                                <i className={`bi ${m.icon}`}></i>
                            </div>
                            <h4>{m.title}</h4>
                            <p>{m.desc}</p>
                        </div>
                    ))}
                </div>
            </div>

            {/* ── 3. Why Different ── */}
            <div className="about-why-wrap">
                <div className="about-why-header">
                    <h2>Why we are different from others?</h2>
                    <p>We stand out from the competition with our unique blend of cutting-edge solutions and reasonable pricing.</p>
                </div>
                <div className="about-why-grid">
                    {differentiators.map((item, i) => (
                        <div className="about-why-card" key={i}>
                            <div className="about-why-icon">
                                <i className={`bi ${item.icon}`}></i>
                            </div>
                            <h4>{item.title}</h4>
                            <p>{item.desc}</p>
                        </div>
                    ))}
                </div>
            </div>

            {/* ── 4. Mission & Vision ── */}
            <div className="about-mv-wrap">
                <div className="about-mv-card vision">
                    <div className="mv-icon"><i className="bi bi-eye-fill"></i></div>
                    <span className="about-eyebrow">OUR VISION</span>
                    <h3>To empower every business owner with real-time financial intelligence, right in the palm of their hand</h3>

                </div>
                <div className="about-mv-card mission">
                    <div className="mv-icon"><i className="bi bi-rocket-takeoff-fill"></i></div>
                    <span className="about-eyebrow">OUR MISSION</span>
                    <h3>To simplify complex data into actionable insights through a secure, intuitive mobile interface, enabling entrepreneurs to make faster, data-driven decisions anytime, anywhere</h3>
                </div>
            </div>

            {/* ── 5. Our Core Values ── */}
            <div className="about-values-wrap">
                <div className="about-values-header">
                    <span className="about-eyebrow">OUR VALUES</span>
                    <h2>The principles that guide us</h2>
                    <p>Our core values reflect our commitment to integrity, innovation, and our values shape the way we build, collaborate, and deliver meaningful solutions.</p>
                </div>
                <div className="about-values-grid">
                    {coreValues.map((value, i) => (
                        <div className="value-card" key={i}>
                            <div className="value-icon">
                                <i className={`bi ${value.icon}`}></i>
                            </div>
                            <div className="value-content">
                                <h4>{value.title}</h4>
                                <span className="value-tagline">{value.tagline}</span>
                                <p>{value.desc}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* ── 5. Meet Our Team ── */}

            <div className="about-team-wrap">
                <div className="about-team-header">
                    <span className="about-eyebrow">THE TEAM</span>
                    <h2>Meet the people behind FinlyticZ</h2>
                    <p>A passionate team of innovators, designers, and engineers building the future of finance.</p>
                </div>
                <div className="about-team-grid">
                    {teamMembers.map((member, i) => (
                        <div className="team-card" key={i}>
                            <div className="team-avatar">
                                <span>{member.initials}</span>
                            </div>
                            <h4>{member.name}</h4>
                            <p className="team-role">{member.role}</p>
                            <div className="team-socials">
                                <a href={member.linkedin} aria-label="LinkedIn"><i className="bi bi-linkedin"></i></a>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

        </section>
    );
};

export default AboutUs;
