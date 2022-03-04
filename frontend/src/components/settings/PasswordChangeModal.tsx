import { Button, Form, message, Modal } from 'antd';
import { useForm } from 'antd/lib/form/Form';
import React, { useContext, useState } from 'react';
import { changePassword, getRequestError } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { getErrorMessageString } from '../../types/RequestError';
import PasswordWithConfirm, { PasswordFieldProps } from '../register/PasswordWithConfirm';

type Props = {
    visible: boolean,
    onClose: () => void
}

const ChangePasswordModal: React.FC<Props> = ({ visible, onClose }) => {

    const [form] = useForm();
    const [loading, setLoading] = useState(false);
    const authContext = useContext(AuthContext);

    const onFormSubmit = (values: any) => {
        setLoading(true);
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
            changePassword(values.password)
                .then(user => {
                    setLoading(false);
                    authContext.login(user);
                    message.success("Passwort erfolgreich geändert");
                    cleanupAndClose();
                }).catch(err => {
                    setLoading(false);
                    const reqErr = getRequestError(err);
                    message.error(getErrorMessageString(reqErr.errorCode));
                });
        }
    };

    const cleanupAndClose = () => {
        form.resetFields();
        onClose();
    };

    const [passwordFieldsInfo, setPasswordFieldsInfo] = useState<{
        validateStatus: PasswordFieldProps['validateStatus'],
        message: PasswordFieldProps['message']
    }>({
        validateStatus: undefined,
        message: undefined
    });

    return (
        <Modal
            title="Passwort ändern"
            visible={visible}
            onCancel={cleanupAndClose}
            width={800}
            footer={[
                <Button
                    type='primary'
                    htmlType='submit'
                    onClick={e => form.submit()}
                    loading={loading}>
                    Passwort ändern
                </Button>
            ]}>
            <Form
                form={form}
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 14 }}
                onFinish={onFormSubmit}>
                <PasswordWithConfirm.Password
                    disabled={loading}
                    customLable="Neues Passwort"
                    validateStatus={passwordFieldsInfo.validateStatus}
                    message={passwordFieldsInfo.message}
                />
                <PasswordWithConfirm.PasswordConfirm
                    disabled={loading}
                    customLable="Neues Passwort wiederholen"
                    validateStatus={passwordFieldsInfo.validateStatus}
                    message={passwordFieldsInfo.message}
                />
            </Form>
        </Modal>
    );
}

export default ChangePasswordModal;