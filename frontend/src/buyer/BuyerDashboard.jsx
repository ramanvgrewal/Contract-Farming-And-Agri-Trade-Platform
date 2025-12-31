import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';

const BuyerDashboard = () => {
    const [requirements, setRequirements] = useState([]);
    const [marketplaceListings, setMarketplaceListings] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [reqRes, listRes] = await Promise.all([
                    axiosInstance.get('/contracts/requirements'),
                    axiosInstance.get('/marketplace/listings')
                ]);
                setRequirements(reqRes.data);
                setMarketplaceListings(listRes.data);
            } catch (err) {
                console.error(err);
            }
        };
        fetchData();
    }, []);

    return (
        <div className="dashboard">
            <h2>Buyer Dashboard</h2>
            <div className="stats-cards">
                <div className="card">
                    <h3>My Requirements</h3>
                    <p>{requirements.length}</p>
                    <Link to="/buyer/requirements">Manage Requirements</Link>
                </div>
                <div className="card">
                    <h3>Marketplace</h3>
                    <p>{marketplaceListings.length} Active Listings</p>
                    <Link to="/buyer/requirements">Go to Marketplace</Link>
                </div>
            </div>

            <section>
                <h3>Available for Negotiation</h3>
                <div className="listings-grid">
                    {marketplaceListings.map(item => (
                        <div key={item.id} className="card">
                            <h4>{item.cropName}</h4>
                            <p>${item.expectedPrice}</p>
                            <Link to={`/negotiate/${item.id}`} className="btn-negotiate">Negotiate</Link>
                        </div>
                    ))}
                </div>
            </section>
        </div>
    );
};

export default BuyerDashboard;
