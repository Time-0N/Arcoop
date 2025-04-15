import './Shop.css';
import { useEffect, useState } from "react";

type Color = {
    id: number,
    colorName: string,
    colorCode: string,
    price: number,
    owned?: boolean,
    equipped?: boolean
}

function Shop() {
    const [shopColors, setShopColors] = useState<Color[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch("/api/shop/colors", {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + localStorage.getItem("jwt"),
                },
            });

            const data = await response.json();
            console.log(data);
            setShopColors(data);
        };
        fetchData();
    }, []);

    const handleBuy = async (colorId: number) => {
        try {
            const response = await fetch("/api/shop/colors/buy", {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + localStorage.getItem("jwt"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(colorId),
            });

            if (response.ok) {
                console.log("Color bought successfully!");
                setShopColors((prevColors) =>
                    prevColors.map((color) =>
                        color.id === colorId ? { ...color, owned: true } : color
                    )
                );
            } else {
                console.error("Error buying color:", response.status);
            }
        } catch (error) {
            console.error("Error buying color:", error);
        }
    };

    const handleEquip = async (colorId: number) => {
        try {
            const response = await fetch("/api/shop/colors/equip", {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + localStorage.getItem("jwt"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(colorId),
            });

            if (response.ok) {
                console.log("Color equipped successfully");
                setShopColors((prevColors) =>
                    prevColors.map((color) =>
                        color.id === colorId ? { ...color, owned: true, equipped: true } : { ...color, equipped: false }
                    )
                );
            } else {
                console.error("Error equipping color:", response.status);
            }
        } catch (error) {
            console.error("Error equipping color:", error);
        }
    };

    return (
        <div className="shop-container">
            <div className="shop-content">
                {shopColors.map((color) => (
                    <div key={color.id} className={`color-item ${color.equipped ? 'equipped' : ''}`}>
                        <div className="color-code" style={{ color: color.colorCode }}>■</div>
                        <div className="color-name" style={{ color: color.colorCode }}>{color.colorName}</div>
                        <div className="color-price">{color.price}</div>
                        {color.owned ? (
                            <button className="equip-button" disabled={color.equipped} onClick={() => handleEquip(color.id)}>Equip</button>
                        ) : (
                            <button className="buy-button" onClick={() => handleBuy(color.id)}>Buy</button>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default Shop;
