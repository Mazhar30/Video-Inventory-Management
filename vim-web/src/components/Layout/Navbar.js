import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

const Navbar = ({ role, onLogout }) => {
    const navigate = useNavigate();

    const handleLogout = () => {
        onLogout();  // Call the onLogout function passed as prop
        navigate('/login');  // Navigate to the login page
    };

    return (
        <nav style={{ padding: '10px', backgroundColor: '#333', color: '#fff' }}>
            {role ? (
                role === 'ADMIN' ? (
                    <>
                        <Link to="/admin/video-management" style={{ color: '#fff', marginRight: '15px' }}>Video Management</Link>
                        <Link to="/admin/upload" style={{ color: '#fff', marginRight: '15px' }}>Upload Video</Link>
                        <Link to="/admin/assign" style={{ color: '#fff', marginRight: '15px' }}>Assign Videos</Link>
                        <Link to="/admin/activity-log" style={{ color: '#fff', marginRight: '15px' }}>Activity Log</Link>
                        <button onClick={handleLogout} style={{ color: '#fff', background: 'none', border: 'none', cursor: 'pointer' }}>
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/user/assigned-videos" style={{ color: '#fff', marginRight: '15px' }}>Assigned Videos</Link>
                        <button onClick={handleLogout} style={{ color: '#fff', background: 'none', border: 'none', cursor: 'pointer' }}>
                            Logout
                        </button>
                    </>
                )
            ) : (
                <>
                    <Link to="/login" style={{ color: '#fff', marginRight: '15px' }}>Login</Link>
                    <Link to="/register" style={{ color: '#fff', marginRight: '15px' }}>Register</Link>
                </>
            )}
        </nav>
    );
};

export default Navbar;