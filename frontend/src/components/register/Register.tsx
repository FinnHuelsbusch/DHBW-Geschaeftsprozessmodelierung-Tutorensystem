import { Button, Divider, Form, Input, message, Result, } from 'antd';
import Paragraph from 'antd/lib/typography/Paragraph';
import Text from 'antd/lib/typography/Text';
import Title from 'antd/lib/typography/Title';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRequestError, register } from '../../api/api';
import { AppRoutes } from '../../types/AppRoutes';
import EmailFormInput from '../inputs/EmailFormInput';

const Register: React.FC = () => {

    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [showRegisterMessage, setShowRegisterMessage] = useState(false);

    const onRegister = (values: any) => {
        setLoading(true);
        register(values.email, values.password)
            .then(res => {
                console.log("res", res);
                setLoading(false);
                setShowRegisterMessage(true);
            }, err => {
                const reqErr = getRequestError(err);
                message.error(`${reqErr.reason}`);
                setLoading(false);
            });
    };

    const UserPasswordForm = () => (
        <Form
            name="login"
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 10 }}
            onFinish={onRegister}>
            <EmailFormInput />
            <Form.Item
                label="Passwort"
                name="password"
                rules={[{ required: true, message: 'Pflichtfeld' }]}>
                <Input.Password />
            </Form.Item>

            <Form.Item
                label="Passwort (bestätigen)"
                name="password"
                rules={[{ required: true, message: 'Pflichtfeld' }]}>
                <Input.Password />
            </Form.Item>
            <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                <Button loading={loading} htmlType='submit' type='primary'>
                    Registrieren
                </Button>
                <Divider />
                Oder <a onClick={e => navigate(AppRoutes.Main.Subroutes.Login, { replace: true })}>
                    anmelden.
                </a>
            </Form.Item>
        </Form >
    );

    const RegisterMessage = () => (
        <Result
            status="success"
            title="Registrierung erfolgreich"
            extra={
                <Paragraph>
                    <Text style={{ fontSize: 16 }}>
                        An die angegebene E-Mail Adresse wurde eine Nachricht
                        mit einem Bestätigungslink gesendet.
                    </Text>
                    <br />
                    <Text style={{ fontSize: 16 }}>
                        Über den enthaltenen Link können Sie ein Konto erstellen.
                    </Text>
                </Paragraph>
            }>
        </Result>
    );

    return (
        <>
            <Title level={1}>
                Registrierung
            </Title>
            {showRegisterMessage ? <RegisterMessage /> : <UserPasswordForm />}
        </>
    )
}

export default Register;