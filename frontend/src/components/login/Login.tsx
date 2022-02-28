import { Button, Divider, Form, Input, message, } from 'antd';
import Title from 'antd/lib/typography/Title';
import React, { useContext, useState } from 'react';
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { login } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { AppRoutes } from '../../types/AppRoutes';


const Login: React.FC = () => {

    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    const [isRegister, setIsRegister] = useState(false);

    const onSubmit = (values: any) => {
        login(values.email, values.password)
            .then(user => {
                authContext.login(user);
                message.success("Login erfolgreich", 2);
                navigate(AppRoutes.Home);
            }).catch(err => message.error("Login fehlgeschlagen"));
    };

    return (
        <>
            <Title level={1}>
                {isRegister ? "Registrierung" : "Anmeldung"}
            </Title>
            <Form
                name="login"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 10 }}
                onFinish={onSubmit}>
                <Form.Item
                    label="E-Mail"
                    name="email"
                    rules={[{ required: true, message: 'Pflichtfeld' }]}>
                    <Input type={'email'} />
                </Form.Item>
                <Form.Item
                    label="Passwort"
                    name="password"
                    rules={[{ required: true, message: 'Pflichtfeld' }]}>
                    <Input.Password />
                </Form.Item>
                {isRegister &&
                    <Form.Item
                        label="Passwort (bestätigen)"
                        name="password"
                        rules={[{ required: true, message: 'Pflichtfeld' }]}>
                        <Input.Password />
                    </Form.Item>}
                <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                    <Button htmlType='submit' type='primary'>
                        {isRegister ? "Registrieren" : "Anmelden"}
                    </Button>
                    <Divider />
                    Oder <a onClick={e => setIsRegister(!isRegister)}>
                        {isRegister ? "anmelden." : "registrieren."}
                    </a>
                </Form.Item>
            </Form>
        </>
    )
}

export default Login;