import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const RoleGuard = ({ children, allowedRoles }) => {
    const { user } = useAuth();

    // Spring Boot roles might be like ROLE_FARMER
    const userRole = user?.role?.startsWith('ROLE_') ? user.role.substring(5) : user?.role;

    if (!allowedRoles.includes(userRole)) {
        return <Navigate to="/" replace />;
    }

    return children;
};

export default RoleGuard;
