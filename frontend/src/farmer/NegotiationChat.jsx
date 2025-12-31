import React, { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { useAuth } from '../context/AuthContext';

const NegotiationChat = () => {
    const { listingId } = useParams();
    const { user } = useAuth();
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [price, setPrice] = useState('');
    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws-negotiation');
        stompClient.current = Stomp.over(socket);

        stompClient.current.connect({}, () => {
            stompClient.current.subscribe(`/topic/negotiation/${listingId}`, (sdkEvent) => {
                onMessageReceived(sdkEvent);
            });
        }, (error) => {
            console.error("STOMP error " + error);
        });

        return () => {
            if (stompClient.current) {
                stompClient.current.disconnect();
            }
        };
    }, [listingId]);

    useEffect(scrollToBottom, [messages]);

    const onMessageReceived = (payload) => {
        const message = JSON.parse(payload.body);
        setMessages(prev => [...prev, message]);
    };

    const sendMessage = (type) => {
        if (stompClient.current && stompClient.current.connected) {
            const chatMessage = {
                listingId: listingId,
                senderEmail: user.email,
                content: newMessage,
                price: price || null,
                type: type
            };
            stompClient.current.send(`/app/negotiate/${listingId}`, {}, JSON.stringify(chatMessage));
            setNewMessage('');
            if (type === 'OFFER') setPrice('');
        }
    };

    return (
        <div className="negotiation-container">
            <h3>Real-time Negotiation - Listing {listingId}</h3>
            <div className="chat-window">
                {messages.map((msg, index) => (
                    <div key={index} className={`message ${msg.senderEmail === user.email ? 'mine' : 'theirs'}`}>
                        <strong>{msg.senderEmail} ({msg.type}):</strong>
                        <p>{msg.content}</p>
                        {msg.price && <span className="price-tag">${msg.price}</span>}
                    </div>
                ))}
                <div ref={messagesEndRef} />
            </div>
            <div className="input-area">
                <input 
                    type="text" 
                    placeholder="Type message..." 
                    value={newMessage} 
                    onChange={(e) => setNewMessage(e.target.value)} 
                />
                <input 
                    type="number" 
                    placeholder="Offer Price" 
                    value={price} 
                    onChange={(e) => setPrice(e.target.value)} 
                />
                <button onClick={() => sendMessage('CHAT')}>Send Chat</button>
                <button onClick={() => sendMessage('OFFER')} className="btn-offer">Send Offer</button>
            </div>
        </div>
    );
};

export default NegotiationChat;
