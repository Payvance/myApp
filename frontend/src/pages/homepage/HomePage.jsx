import React from 'react';
import HomePageContainer from '../../components/homepage/homepageContainer/HomePageContainer';
import Navbar from '../../components/homepage/navbar/Navbar';
import HomeTab from '../../components/homepage/hometab/Hometab';
import './HomePage.css';

const HomePage = () => {
    return (
        <HomePageContainer>
            <Navbar />
            <main className="homepage-main">
                <HomeTab />
            </main>
        </HomePageContainer>
    );
};

export default HomePage;
