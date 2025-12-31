import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';

const ProduceListings = () => {
    const [listings, setListings] = useState([]);
    const [formData, setFormData] = useState({
        cropName: '',
        quantity: '',
        qualityGrade: '',
        expectedPrice: ''
    });

    const fetchListings = async () => {
        try {
            const res = await axiosInstance.get('/marketplace/listings');
            setListings(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchListings();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axiosInstance.post('/marketplace/listings', formData);
            setFormData({ cropName: '', quantity: '', qualityGrade: '', expectedPrice: '' });
            fetchListings();
        } catch (err) {
            alert('Failed to create listing');
        }
    };

    return (
        <div className="feature-page">
            <h2>Produce Listings</h2>
            <form onSubmit={handleSubmit} className="listing-form">
                <input placeholder="Crop Name" value={formData.cropName} onChange={e => setFormData({...formData, cropName: e.target.value})} required />
                <input type="number" placeholder="Quantity (kg)" value={formData.quantity} onChange={e => setFormData({...formData, quantity: e.target.value})} required />
                <input placeholder="Quality Grade" value={formData.qualityGrade} onChange={e => setFormData({...formData, qualityGrade: e.target.value})} required />
                <input type="number" placeholder="Expected Price" value={formData.expectedPrice} onChange={e => setFormData({...formData, expectedPrice: e.target.value})} required />
                <button type="submit">List Produce</button>
            </form>

            <div className="list-container">
                {listings.map(item => (
                    <div key={item.id} className="item-card">
                        <h4>{item.cropName}</h4>
                        <p>Quantity: {item.quantity} kg</p>
                        <p>Grade: {item.qualityGrade}</p>
                        <p>Price: ${item.expectedPrice}</p>
                        <p>Status: {item.status}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ProduceListings;
