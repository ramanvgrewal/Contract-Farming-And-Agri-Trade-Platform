import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';

const FarmerDashboard = () => {
    const [myListings, setMyListings] = useState([]);

    useEffect(() => {
        const fetchListings = async () => {
            try {
                const res = await axiosInstance.get('/marketplace/listings');
                // Normally backend should have /marketplace/listings/my
                // Filtering for demo if endpoint not available
                setMyListings(res.data);
            } catch (err) {
                console.error(err);
            }
        };
        fetchListings();
    }, []);

    return (
        <div className="dashboard">
            <h2>Farmer Dashboard</h2>
            <div className="stats-cards">
                <div className="card">
                    <h3>Active Listings</h3>
                    <p>{myListings.length}</p>
                    <Link to="/farmer/listings">Manage Listings</Link>
                </div>
                <div className="card">
                    <h3>New Offers</h3>
                    <p>Check your listings for offers</p>
                </div>
            </div>

            <section>
                <h3>Recent Activity</h3>
                <ul>
                    {myListings.slice(0, 5).map(item => (
                        <li key={item.id}>{item.cropName} - {item.quantity} kg - {item.status}</li>
                    ))}
                </ul>
            </section>
        </div>
    );
};

export default FarmerDashboard;
