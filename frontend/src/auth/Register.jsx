import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { registerApi } from '../api/authApi';

const Register = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        role: 'FARMER',
        name: '',
        phone: '',
        address: '',
        city: '',
        state: ''
    });
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await registerApi(formData);
            login(response.data.token);
            navigate('/');
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed');
        }
    };

    return (
        <div className="auth-container">
            <h2>Create an Account</h2>
            {error && <p className="error">{error}</p>}
            <form onSubmit={handleSubmit}>
                <input name="name" placeholder="Full Name" onChange={handleChange} required />
                <input name="email" type="email" placeholder="Email" onChange={handleChange} required />
                <input name="password" type="password" placeholder="Password" onChange={handleChange} required />
                <select name="role" onChange={handleChange}>
                    <option value="FARMER">Farmer</option>
                    <option value="BUYER">Buyer</option>
                </select>
                <input name="phone" placeholder="Phone" onChange={handleChange} />
                <input name="address" placeholder="Address" onChange={handleChange} />
                <input name="city" placeholder="City" onChange={handleChange} />
                <input name="state" placeholder="State" onChange={handleChange} />
                <button type="submit">Register</button>
            </form>
            <p>Already have an account? <Link to="/login">Login here</Link></p>
        </div>
    );
};

export default Register;
