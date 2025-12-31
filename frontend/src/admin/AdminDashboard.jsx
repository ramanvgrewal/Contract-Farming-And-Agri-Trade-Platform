import React from 'react';

const AdminDashboard = () => {
    return (
        <div className="dashboard">
            <h2>Admin Overview</h2>
            <div className="stats-cards">
                <div className="card">
                    <h3>System Users</h3>
                    <p>Total: 150</p>
                </div>
                <div className="card">
                    <h3>Active Contracts</h3>
                    <p>45</p>
                </div>
                <div className="card">
                    <h3>Marketplace Deals</h3>
                    <p>28</p>
                </div>
            </div>
            <p>Admin functionality is currently read-only.</p>
        </div>
    );
};

export default AdminDashboard;
