import React from 'react';
import './HomeTab.css';
import Hero from './Hero';
import AboutUs from '../aboutus/AboutUs';
import Features from '../features/Feature';
import PricePlan from '../priceplan/PricePlan';
import ContactUs from '../contactus/ContactUs';
import Footer from '../footer/Footer';

const HomeTab = () => {
    return (
        <div className="hometab">
            <Hero />
            <AboutUs />
            <Features />
            <PricePlan />
            <ContactUs />
            <Footer />
        </div>
    );
};

export default HomeTab;
