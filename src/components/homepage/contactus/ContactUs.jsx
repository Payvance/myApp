import React, { useState, useEffect } from 'react';
import './ContactUs.css';
import { companyConfigServices } from '../../../services/apiService';

const ContactUs = () => {
    const [form, setForm] = useState({
        name: '',
        email: '',
        phone: '',
        subject: '',
        message: ''
    });

    const [sent, setSent] = useState(false);
    const [companyData, setCompanyData] = useState({});


    const handleChange = (e) =>
        setForm({ ...form, [e.target.name]: e.target.value });

    // 🔹 Fetch Company Details on Load
    useEffect(() => {
        const fetchCompanyDetails = async () => {
            try {
                const response = await companyConfigServices.getCompanyDetails();

                // Convert array to object
                const formattedData = {};
                if (response.data && Array.isArray(response.data)) {
                    response.data.forEach(item => {
                        formattedData[item.code] = item.value;
                    });
                }

                setCompanyData(formattedData);

            } catch (error) {
                console.error("Error fetching company details:", error);
            }
        };

        fetchCompanyDetails();
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();
        setSent(true);
        setForm({
            name: '',
            email: '',
            phone: '',
            subject: '',
            message: ''
        });
        setTimeout(() => setSent(false), 4000);
    };

    return (
        <section className="cu-section" id="contact">

            <div className="cu-header">
                <h1 className="cu-main-title">Contact Us</h1>
                <p className="cu-main-sub">
                    Have a question or need support? Get in touch with our team.
                </p>
            </div>

            <div className="cu-body">

                {/* Left — Contact Info */}
                <div className="cu-info-col">

                    <div className="cu-info-card">
                        <div className="cu-info-icon">
                            <i className="bi bi-geo-alt-fill"></i>
                        </div>
                        <div>
                            <h4>Office Address</h4>
                            <p>
                                {companyData.companyName}<br />
                                {companyData.address}
                            </p>
                        </div>
                    </div>

                    <div className="cu-info-card">
                        <div className="cu-info-icon">
                            <i className="bi bi-telephone-fill"></i>
                        </div>
                        <div>
                            <h4>Phone</h4>
                            <p>{companyData.phone}</p>
                        </div>
                    </div>

                    <div className="cu-info-card">
                        <div className="cu-info-icon">
                            <i className="bi bi-envelope-fill"></i>
                        </div>
                        <div>
                            <h4>Email</h4>
                            <p>{companyData.email}</p>
                        </div>
                    </div>

                </div>

                {/* Right — Message Form */}
                <div className="cu-form-col">
                    <div className="cu-form-card">
                        <h3 className="cu-form-title">Leave a Message</h3>

                        {sent && (
                            <div className="cu-success-msg">
                                Message sent! We'll be in touch shortly.
                            </div>
                        )}

                        <form className="cu-form" onSubmit={handleSubmit}>
                            <div className="cu-form-row">
                                <div className="cu-field">
                                    <label htmlFor="cu-name">Full Name *</label>
                                    <input
                                        id="cu-name"
                                        name="name"
                                        type="text"
                                        placeholder="Enter your full name"
                                        value={form.name}
                                        validationType="TEXT_ONLY"
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                                <div className="cu-field">
                                    <label htmlFor="cu-email">Email *</label>
                                    <input
                                        id="cu-email"
                                        name="email"
                                        type="email"
                                        placeholder="you@company.com"
                                        value={form.email}
                                        onChange={handleChange}
                                        validationType="EMAIL"
                                        required
                                    />
                                </div>
                            </div>

                            <div className="cu-form-row">
                                <div className="cu-field">
                                    <label htmlFor="cu-phone">Phone Number</label>
                                    <input
                                        id="cu-phone"
                                        name="phone"
                                        type="tel"
                                        placeholder="Enter your phone number"
                                        value={form.phone}
                                        validationType="NUMBER_ONLY"
                                        onChange={handleChange}
                                    />
                                </div>
                                <div className="cu-field">
                                    <label htmlFor="cu-subject">Subject</label>
                                    <input
                                        id="cu-subject"
                                        name="subject"
                                        type="text"
                                        placeholder="How can we help?"
                                        value={form.subject}
                                        onChange={handleChange}
                                    />
                                </div>
                            </div>

                            <div className="cu-field cu-field-full">
                                <label htmlFor="cu-message">Message *</label>
                                <textarea
                                    id="cu-message"
                                    name="message"
                                    rows={5}
                                    placeholder="Describe your query in detail..."
                                    value={form.message}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <button type="submit" className="cu-submit-btn">
                                Send Message <i className="bi bi-send-fill"></i>
                            </button>
                        </form>
                    </div>
                </div>

            </div>
        </section>
    );
};

export default ContactUs;
