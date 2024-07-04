import React, { useState } from 'react';
import { Form, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const LoginForm = ({ onLogin, isRegister }) => {
    const { t } = useTranslation();
    const [formData, setFormData] = useState({
        email: '',
        pass: '',
        confirmpass: isRegister ? '' : undefined,
        name: isRegister ? '' : undefined,
        surname: isRegister ? '' : undefined
    });
    const navigate = useNavigate();
    const [errors, setErrors] = useState({
        email: '',
        pass: '',
        confirmpass: '',
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const newErrors = { email: '', pass: '', confirmpass: '' };

        if (!validateEmail(formData.email)) {
            newErrors.email = t('login.invalidEmail');
        }
        if (!validatePasswordLength(formData.pass)) {
            newErrors.pass = t('login.shortPass');
        }
        if (isRegister && formData.pass !== formData.confirmpass) {
            newErrors.confirmpass = t('login.confirmPassError');
        }

        setErrors(newErrors);
        if (!newErrors.email && !newErrors.pass && (!isRegister || !newErrors.confirmpass)) {
            if (isRegister) {
                registerUser(formData);
            } else {
                loginUser(formData);
            }
        }
    };

    const validateEmail = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };

    const validatePasswordLength = (pass) => {
        return pass.length >= 6; // Example minimum length
    };
    const loginUser = (formData) => {
        fetch(`${import.meta.env.VITE_API_URL}/users/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'client': 'WEB'
            },
            body: JSON.stringify({ email: formData.email, pass: formData.pass }),
        })
            .then((response) => {
                if (response.ok) {
                    return response.json(); // Return the parsed JSON promise
                } else if (response.status === 401) {
                    throw new Error(t("login.badRequestLogin"));
                } else {
                    throw new Error(t("login.errorLoggingIn"));
                }
            })
            .then((data) => {
                // Now we have the parsed JSON data
                localStorage.setItem('authToken', data.sessionId);
                localStorage.setItem('emailUser', formData.email);
                onLogin(formData.email);
                navigate('/');
            })
            .catch((error) => {
                console.error('Error:', error);
                alert(error.message);
            });
            
    };

    const registerUser = (formData) => {
        fetch(`${import.meta.env.VITE_API_URL}/users/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: formData.email,
                pass: formData.pass,
                name: formData.name,
                surname: formData.surname
            }),
        })
            .then((response) => {
                if (response.status === 201) {
                    alert(t("login.registrationSuccessful"));
                    navigate('/login');
                } else if (response.status === 400) {
                    alert(t("login.badRequestRegister"));
                } else {
                    alert(t("login.errorRegistering"));
                }
            })
            .catch((error) => {
                console.error('Error:', error);
                alert(t("login.errorRegistering"));
            });
    };
    

    return (
        <Form onSubmit={handleSubmit}>
            {isRegister && (
                <>
                    <Form.Group controlId="name">
                        <Form.Label>{t('login.name')}</Form.Label>
                        <Form.Control
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder={t('login.name')}
                            required
                        />
                    </Form.Group>
                    <Form.Group controlId="surname">
                        <Form.Label>{t('login.surname')}</Form.Label>
                        <Form.Control
                            type="text"
                            name="surname"
                            value={formData.surname}
                            onChange={handleChange}
                            placeholder={t('login.surname')}
                            required
                        />
                    </Form.Group>
                </>
            )}
            <Form.Group controlId="email">
                <Form.Label>{t('login.email')}</Form.Label>
                <Form.Control
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder={t('login.enterEmail')}
                    required
                    isInvalid={!!errors.email}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.email}
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group controlId="pass">
                <Form.Label>{t('login.pass')}</Form.Label>
                <Form.Control
                    type="password"
                    name="pass"
                    value={formData.pass}
                    onChange={handleChange}
                    placeholder={t('login.enterPass')}
                    required
                    isInvalid={!!errors.pass}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.pass}
                </Form.Control.Feedback>
            </Form.Group>
            {isRegister && (
                <Form.Group controlId="confirmpass">
                    <Form.Label>{t('login.confirmPass')}</Form.Label>
                    <Form.Control
                        type="password"
                        name="confirmpass"
                        value={formData.confirmpass}
                        onChange={handleChange}
                        placeholder={t('login.confirmPass')}
                        required
                        isInvalid={!!errors.confirmpass}
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.confirmpass}
                    </Form.Control.Feedback>
                </Form.Group>
            )}
            <Button variant="primary" type="submit">
                {isRegister ? t('login.register') : t('login.login')}
            </Button>
        </Form>
    );
};

export default LoginForm;