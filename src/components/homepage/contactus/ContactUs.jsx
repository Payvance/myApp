import React, { useState, useEffect } from 'react';
import './ContactUs.css';
import { companyConfigServices } from '../../../services/apiService';
import { validateField } from '../../../config/validateField';
import { contactUsServices } from '../../../services/apiService';

const ContactUs = () => {
    const [form, setForm] = useState({
        name: '',
        email: '',
        phone: '',
        subject: '',
        message: ''
    });

    const [errors, setErrors] = useState({});
    const [sent, setSent] = useState(false);
    const [companyData, setCompanyData] = useState({});


    const handleChange = (e) => {
        const { name, value } = e.target;
        const validationType = e.target.getAttribute('validationType');
        let finalValue = value;

        // Alphabet-only filtering for Name field
        if (validationType === 'TEXT_ONLY') {
            finalValue = value.replace(/[^A-Za-z\s]/g, "");
        }

        // 10-digit limit for Phone field
        if (validationType === 'NUMBER_ONLY') {
            finalValue = value.replace(/[^0-9]/g, "").slice(0, 10);
        }

        setForm({ ...form, [name]: finalValue });

        // Clear error when user starts typing again
        if (errors[name]) {
            setErrors(prev => {
                const newErrors = { ...prev };
                delete newErrors[name];
                return newErrors;
            });
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            const { name, value } = e.target;
            const validationType = e.target.getAttribute('validationType');

            // Only show error if input is NOT empty
            if (value.trim() !== "" && validationType) {
                const error = validateField(validationType, value, e.target.required);
                setErrors(prev => ({ ...prev, [name]: error }));
            } else if (value.trim() === "") {
                // Clear error if empty (requirement: don't show error when empty)
                setErrors(prev => {
                    const newErrors = { ...prev };
                    delete newErrors[name];
                    return newErrors;
                });
            }
        }
    };

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

    const handleSubmit = async (e) => {
        e.preventDefault();

        const newErrors = {};
        const fieldsToValidate = [
            { name: 'name', type: 'TEXT_ONLY', required: true },
            { name: 'email', type: 'EMAIL', required: true },
            { name: 'phone', type: 'NUMBER_ONLY', required: true },
            { name: 'subject', type: 'EVERYTHING', required: true },
            { name: 'message', type: 'EVERYTHING', required: true },
        ];

        fieldsToValidate.forEach(field => {
            const error = validateField(field.type, form[field.name], field.required);
            if (error) newErrors[field.name] = error;
        });

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        try {

            const payload = {
                fullName: form.name,
                email: form.email,
                phoneNumber: form.phone,
                subject: form.subject,
                message: form.message
            };

            await contactUsServices.createMessage(payload);

            setSent(true);

            setForm({
                name: '',
                email: '',
                phone: '',
                subject: '',
                message: ''
            });

            setErrors({});
            setTimeout(() => setSent(false), 4000);

        } catch (error) {
            console.error("Error sending message:", error);
        }
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
                            <h4>Registered Office Address</h4>
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
                                        className={errors.name ? 'cu-input-error' : ''}
                                        placeholder="Enter your full name"
                                        value={form.name}
                                        validationType="TEXT_ONLY"
                                        onChange={handleChange}
                                        onKeyDown={handleKeyDown}
                                        required
                                    />
                                    {errors.name && <span className="cu-error-msg">{errors.name}</span>}
                                </div>
                                <div className="cu-field">
                                    <label htmlFor="cu-email">Email *</label>
                                    <input
                                        id="cu-email"
                                        name="email"
                                        type="email"
                                        className={errors.email ? 'cu-input-error' : ''}
                                        placeholder="you@company.com"
                                        value={form.email}
                                        onChange={handleChange}
                                        onKeyDown={handleKeyDown}
                                        validationType="EMAIL"
                                        required
                                    />
                                    {errors.email && <span className="cu-error-msg">{errors.email}</span>}
                                </div>
                            </div>

                            <div className="cu-form-row">
                                <div className="cu-field">
                                    <label htmlFor="cu-phone">Phone Number *</label>
                                    <input
                                        id="cu-phone"
                                        name="phone"
                                        type="tel"
                                        className={errors.phone ? 'cu-input-error' : ''}
                                        placeholder="Enter your phone number"
                                        value={form.phone}
                                        validationType="NUMBER_ONLY"
                                        onChange={handleChange}
                                        onKeyDown={handleKeyDown}
                                        required
                                    />
                                    {errors.phone && <span className="cu-error-msg">{errors.phone}</span>}
                                </div>
                                <div className="cu-field">
                                    <label htmlFor="cu-subject">Subject *</label>
                                    <input
                                        id="cu-subject"
                                        name="subject"
                                        type="text"
                                        className={errors.subject ? 'cu-input-error' : ''}
                                        placeholder="How can we help?"
                                        value={form.subject}
                                        onChange={handleChange}
                                        onKeyDown={handleKeyDown}
                                        validationType="EVERYTHING"
                                        required
                                    />
                                    {errors.subject && <span className="cu-error-msg">{errors.subject}</span>}
                                </div>
                            </div>

                            <div className="cu-field cu-field-full">
                                <label htmlFor="cu-message">Message *</label>
                                <textarea
                                    id="cu-message"
                                    name="message"
                                    rows={3}
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

            {/* Trademark Disclaimer */}
            <div className="cu-footer-disclaimer">
                <p className="cu-disclaimer-text">
                    Tally is a registered trademark of its respective owner. This website, mobile application, and any of our affiliated platforms are not associated with, endorsed by, or sponsored by the trademark owner in any manner. The trademark is referenced only for identification purposes and is used in good faith, following fair and honest practices, without any intention to mislead users, gain unfair benefit from the trademark’s reputation, or damage the goodwill of the trademark holder.
                </p>
            </div>
        </section>
    );
};

export default ContactUs;
