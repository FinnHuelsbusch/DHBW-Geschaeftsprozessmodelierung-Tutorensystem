import { Button, Checkbox, Divider, Form, Input, message, } from 'antd';
import { useForm } from 'antd/lib/form/Form';
import Title from 'antd/lib/typography/Title';
import React, { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRequestError, login } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { AppRoutes } from '../../types/AppRoutes';
import { getErrorMessageString } from '../../types/RequestError';
import EmailFormInput from '../inputs/EmailFormInput';
import { PasswordWithConfirm } from '../inputs/PasswordInput';
import ForgotPasswordModal from './ForgotPasswordModal';


const Login: React.FC = () => {

    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    const [loading, setLoading] = useState(false);
    const [showForgotPasswordModal, setShowForgotPasswordModal] = useState(false);

    const LoginForm = () => {
        const [form] = useForm();

        const onSubmit = (values: any) => {
            setLoading(true);
            login(values.email, values.password)
                .then(user => {
                    navigate(AppRoutes.Main.Path);
                    authContext.login(user, values.rememberLogin);
                    message.success("Login erfolgreich", 2);
                    setLoading(false);
                }).catch(err => {
                    const reqErr = getRequestError(err);
                    message.error(getErrorMessageString(reqErr.errorCode));
                    setLoading(false);
                });
        };

        return (
            <Form
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 10 }}
                form={form}
                onFinish={onSubmit}>

                <EmailFormInput required disabled={loading} />

                <PasswordWithConfirm.Password
                    disabled={loading}
                />

                <Form.Item
                    name="rememberLogin"
                    initialValue={false}
                    valuePropName="checked"
                    wrapperCol={{ offset: 8, span: 10 }}>
                    <Checkbox disabled={loading}>Anmeldung speichern</Checkbox>
                </Form.Item>

                <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                    <a onClick={e => setShowForgotPasswordModal(true)}>
                        Passwort vergessen?
                    </a>
                </Form.Item>

                <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                    <Button loading={loading} htmlType='submit' type='primary'>
                        Anmelden
                    </Button>
                    <Divider />
                    Oder <a onClick={e => navigate(AppRoutes.Main.Subroutes.Register, { replace: true })}>
                        registrieren.
                    </a>
                </Form.Item>

            </Form>
        )
    };

    return (
        <>
            <Title level={1}>
                Anmeldung
            </Title>
            <LoginForm />
            <ForgotPasswordModal
                visible={showForgotPasswordModal}
                onClose={() => setShowForgotPasswordModal(false)} />
        </>
    )
}

export default Login;