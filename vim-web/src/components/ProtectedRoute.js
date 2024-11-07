import React, { useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { getRole } from '../api/api'; 

const ProtectedRoute = ({ children, requiredRole, role, setRole }) => {
    const [loading, setLoading] = useState(true);  // State to track role fetching
    const [error, setError] = useState(null);  // For error handling if role fetching fails
    const navigate = useNavigate();

    useEffect(() => {
        const fetchRole = async () => {
            if (role === null) {
                try {
                    const userRole = await getRole(); 
                    setRole(userRole);
                } catch (err) {
                    setError('Failed to fetch role');
                    console.error("Error fetching role:", err);
                    navigate('/login');
                } finally {
                    setLoading(false);
                }
            } else {
                setLoading(false);
            }
        };

        fetchRole();
    }, [role, setRole]); 

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>{error}</div>; 
    }

    if (role === requiredRole) {
        return children; 
    }

    if (role === 'ADMIN') {
        return <Navigate to="/admin/video-management" />;
    } else if (role === 'USER') {
        return <Navigate to="/user/assigned-videos" />;
    } else {
        return <Navigate to="/login" />;
    }
};

export default ProtectedRoute;