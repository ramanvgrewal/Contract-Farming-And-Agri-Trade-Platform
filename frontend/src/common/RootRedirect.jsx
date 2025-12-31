import React from 'react';
import { Navigate } from 'react-router-dom';

const RootRedirect = ({ user }) => {
    if (!user) return <Navigate to="/login" />;
    
    const role = user.role?.startsWith('ROLE_') ? user.role.substring(5) : user.role;
    
    switch (role) {
        case 'FARMER': return <Navigate to="/farmer" />;
        case 'BUYER': return <Navigate to="/buyer" />;
        case 'ADMIN': return <Navigate to="/admin" />;
        default: return <Navigate to="/login" />;
    }
};

export default RootRedirect;
