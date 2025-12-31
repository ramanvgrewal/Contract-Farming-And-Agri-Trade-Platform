import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';

const CropRequirements = () => {
    const [requirements, setRequirements] = useState([]);
    const [formData, setFormData] = useState({
        cropName: '',
        quantity: '',
        qualityGrade: '',
        season: '',
        minPrice: '',
        maxPrice: ''
    });

    const fetchRequirements = async () => {
        try {
            const res = await axiosInstance.get('/contracts/requirements');
            setRequirements(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchRequirements();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axiosInstance.post('/contracts/requirements', formData);
            setFormData({ cropName: '', quantity: '', qualityGrade: '', season: '', minPrice: '', maxPrice: '' });
            fetchRequirements();
        } catch (err) {
            alert('Failed to post requirement');
        }
    };

    return (
        <div className="feature-page">
            <h2>Buyer Crop Requirements</h2>
            <form onSubmit={handleSubmit} className="requirement-form">
                <input placeholder="Crop Name" value={formData.cropName} onChange={e => setFormData({...formData, cropName: e.target.value})} required />
                <input type="number" placeholder="Quantity (kg)" value={formData.quantity} onChange={e => setFormData({...formData, quantity: e.target.value})} required />
                <input placeholder="Quality Grade" value={formData.qualityGrade} onChange={e => setFormData({...formData, qualityGrade: e.target.value})} required />
                <input placeholder="Season (e.g. Winter 2024)" value={formData.season} onChange={e => setFormData({...formData, season: e.target.value})} required />
                <input type="number" placeholder="Min Price" value={formData.minPrice} onChange={e => setFormData({...formData, minPrice: e.target.value})} />
                <input type="number" placeholder="Max Price" value={formData.maxPrice} onChange={e => setFormData({...formData, maxPrice: e.target.value})} />
                <button type="submit">Post Requirement</button>
            </form>

            <div className="list-container">
                {requirements.map(item => (
                    <div key={item.id} className="item-card">
                        <h4>{item.cropName}</h4>
                        <p>Quantity: {item.quantity} kg</p>
                        <p>Season: {item.season}</p>
                        <p>Range: ${item.minPrice} - ${item.maxPrice}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default CropRequirements;
