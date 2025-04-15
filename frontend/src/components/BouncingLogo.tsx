import React, { useRef, useEffect } from 'react';
import CoopLogo from '../assets/CoopLogo.png';

const BouncingLogo: React.FC = () => {
    const logoRef = useRef<HTMLDivElement>(null);
    let directionX = Math.random() < 0.5 ? -1 : 1;
    let directionY = Math.random() < 0.5 ? -1 : 1;
    let speed = 3;

    useEffect(() => {
        if (logoRef.current) {
            const logo = logoRef.current;
            const width = logo.offsetWidth;
            const height = logo.offsetHeight;
            const maxWidth = window.innerWidth - width;
            const maxHeight = window.innerHeight - height;
            const initialLeft = Math.random() * (maxWidth - width);
            const initialTop = Math.random() * (maxHeight - height);
            logo.style.left = `${initialLeft}px`;
            logo.style.top = `${initialTop}px`;
            const interval = setInterval(() => {
                const left = logo.style.left;
                const top = logo.style.top;
                const newLeft = parseFloat(left.slice(0, -2)) + directionX * speed;
                const newTop = parseFloat(top.slice(0, -2)) + directionY * speed;
                if (newLeft < 0 || newLeft > maxWidth - width) {
                    directionX *= -1;
                }
                if (newTop < 0 || newTop > maxHeight - height) {
                    directionY *= -1;
                }
                logo.style.left = `${newLeft}px`;
                logo.style.top = `${newTop}px`;
            }, 18);
            return () => clearInterval(interval);
        }
    }, []);

    return (
        <div className="bouncing-logo" ref={logoRef}>
            <img src={CoopLogo} alt="Logo" style={{ width: '200px', height: '70px' }}/>
        </div>
    );
};

export default BouncingLogo;