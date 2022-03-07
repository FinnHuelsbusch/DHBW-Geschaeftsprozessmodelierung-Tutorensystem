import { Button, Divider, Form, Input, message, Result, } from 'antd';
import { useForm } from 'antd/lib/form/Form';
import { ValidateStatus } from 'antd/lib/form/FormItem';
import Paragraph from 'antd/lib/typography/Paragraph';
import Text from 'antd/lib/typography/Text';
import Title from 'antd/lib/typography/Title';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRequestError, register } from '../../api/api';
import { AppRoutes } from '../../types/AppRoutes';
import { getErrorMessageString } from '../../types/RequestError';
import EmailFormInput from '../inputs/EmailFormInput';
import PasswordWithConfirm, { PasswordFieldProps } from './PasswordWithConfirm';

const Register: React.FC = () => {

    const navigate = useNavigate();
    const [form] = useForm();
    const [loading, setLoading] = useState(false);
    const [showRegisterMessage, setShowRegisterMessage] = useState(false);

    const [passwordFieldsInfo, setPasswordFieldsInfo] = useState<{
        validateStatus: PasswordFieldProps['validateStatus'],
        message: PasswordFieldProps['message']
    }>({
        validateStatus: undefined,
        message: undefined
    });

    const onSubmit = (values: any) => {
        if (!PasswordWithConfirm.passwordsMatch(values.password, values.passwordConfirm)) {
            setPasswordFieldsInfo({
                validateStatus: 'error',
                message: 'Passwörter stimmen nicht überein'
            });
        } else {
            setPasswordFieldsInfo({
                validateStatus: 'success',
                message: undefined
            });
            setLoading(true);
            register(values.email, values.password)
                .then(res => {
                    setLoading(false);
                    setShowRegisterMessage(true);
                }, err => {
                    const reqErr = getRequestError(err);
                    message.error(getErrorMessageString(reqErr.errorCode));
                    setLoading(false);
                });
        }
    };

    const UserPasswordForm = () => (
        <Form
            name="login"
            form={form}
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 10 }}
            onFinish={onSubmit}>
            <EmailFormInput required disabled={loading} />

            <PasswordWithConfirm.Password
                disabled={loading}
                validateStatus={passwordFieldsInfo.validateStatus}
                message={passwordFieldsInfo.message}
            />

            <PasswordWithConfirm.PasswordConfirm
                disabled={loading}
                validateStatus={passwordFieldsInfo.validateStatus}
                message={passwordFieldsInfo.message}
            />

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