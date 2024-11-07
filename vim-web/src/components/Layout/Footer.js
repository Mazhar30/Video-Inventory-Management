// src/components/Footer.js
import React from 'react';

const Footer = () => {
    return (
        <footer style={{ padding: '10px', backgroundColor: '#333', color: '#fff', textAlign: 'center', position: 'fixed', bottom: 0, width: '100%' }}>
            <p>&copy; {new Date().getFullYear()} My Company. All rights reserved.</p>
        </footer>
    );
};

export default Footer;