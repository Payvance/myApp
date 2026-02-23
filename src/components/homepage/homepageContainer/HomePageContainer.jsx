import React from 'react';
import './HomePageContainer.css';

const HomePageContainer = ({ children }) => {
    return (
        <div className="homepage-container">
            {children}
        </div>
    );
};

export default HomePageContainer;
