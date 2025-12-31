import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Login from './auth/Login';
import Register from './auth/Register';
import ProtectedRoute from './common/ProtectedRoute';
import RoleGuard from './common/RoleGuard';
import RootRedirect from './common/RootRedirect';

// Dashboards
import FarmerDashboard from './farmer/FarmerDashboard';
import BuyerDashboard from './buyer/BuyerDashboard';
import AdminDashboard from './admin/AdminDashboard';

// Features
import ProduceListings from './farmer/ProduceListings';
import CropRequirements from './buyer/CropRequirements';
import NegotiationChat from './farmer/NegotiationChat';

const App = () => {
    const { user, logout, loading } = useAuth();

    if (loading) {
        return <div className="loading-screen">Loading AgriContract...</div>;
    }

    return (
        <div className="app-container">
            <nav className="navbar">
                <h1>AgriContract Platform</h1>
                {user && (
                    <div className="user-info">
                        <span>{user.email} ({user.role})</span>
                        <button onClick={logout} className="logout-btn">Logout</button>
                    </div>
                )}
            </nav>

            <main className="content">
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    {/* Farmer Routes */}
                    <Route path="/farmer" element={
                        <ProtectedRoute>
                            <RoleGuard allowedRoles={['FARMER']}>
                                <FarmerDashboard />
                            </RoleGuard>
                        </ProtectedRoute>
                    } />
                    <Route path="/farmer/listings" element={
                        <ProtectedRoute>
                            <RoleGuard allowedRoles={['FARMER']}>
                                <ProduceListings />
                            </RoleGuard>
                        </ProtectedRoute>
                    } />

                    {/* Buyer Routes */}
                    <Route path="/buyer" element={
                        <ProtectedRoute>
                            <RoleGuard allowedRoles={['BUYER']}>
                                <BuyerDashboard />
                            </RoleGuard>
                        </ProtectedRoute>
                    } />
                    <Route path="/buyer/requirements" element={
                        <ProtectedRoute>
                            <RoleGuard allowedRoles={['BUYER']}>
                                <CropRequirements />
                            </RoleGuard>
                        </ProtectedRoute>
                    } />

                    {/* Shared Negotiation Chat */}
                    <Route path="/negotiate/:listingId" element={
                        <ProtectedRoute>
                            <RoleGuard allowedRoles={['FARMER', 'BUYER']}>
                                <NegotiationChat />
                            </RoleGuard>
                        </ProtectedRoute>
                    } />

                    {/* Admin Routes */}
                    <Route path="/admin" element={
                        <ProtectedRoute>
                            <RoleGuard allowedRoles={['ADMIN']}>
                                <AdminDashboard />
                            </RoleGuard>
                        </ProtectedRoute>
                    } />

                    <Route path="/" element={<RootRedirect user={user} />} />
                </Routes>
            </main>
        </div>
    );
};

export default App;
