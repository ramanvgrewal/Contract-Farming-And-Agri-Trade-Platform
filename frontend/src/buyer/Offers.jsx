import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';

const Offers = () => {
    const [offers, setOffers] = useState([]);

    useEffect(() => {
        const fetchOffers = async () => {
            try {
                // Assuming an endpoint exists or using general marketplace data
                const res = await axiosInstance.get('/marketplace/listings');
                setOffers(res.data);
            } catch (err) {
                console.error(err);
            }
        };
        fetchOffers();
    }, []);

    return (
        <div className="feature-page">
            <h2>Marketplace Offers</h2>
            <div className="list-container">
                {offers.map(item => (
                    <div key={item.id} className="item-card">
                        <h4>{item.cropName}</h4>
                        <p>Listed Price: ${item.expectedPrice}</p>
                        <p>Status: {item.status}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Offers;
