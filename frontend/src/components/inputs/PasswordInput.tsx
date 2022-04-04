import { Form, FormInstance, Input } from 'antd';
import { ValidateStatus } from 'antd/lib/form/FormItem';
import React, { useState } from 'react';
import { validateMessages } from '../../utils/Messages';

export type Props = {
    disabled?: boolean,
    customLabel?: string,
    withConfirmForm?: FormInstance,
    noRegexValidation?: boolean
}

const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/g;

const PasswordInput: React.FC<Props> = ({ disabled = false, customLabel, withConfirmForm, noRegexValidation = false }) => {



    const [passwordValidationStatus, setPasswordValidationStatus] = useState<ValidateStatus|undefined>();
    const [passwordConfirmValidationStatus, setPasswordConfirmValidationStatus] = useState<ValidateStatus|undefined>();

    const passwordsMatch = (password: string, passwordConfirm: string): boolean => {
        return (
            password != null && password.trim() !== ""
            && passwordConfirm === password
        );
    };

    const validatePassword = (rule: any, value: string, callback: any) => {
        if (!value || value === "") {
            setPasswordValidationStatus('error');
            return Promise.reject(validateMessages.required)
        }
        if (!noRegexValidation && !value.match(passwordRegex)) {
            setPasswordValidationStatus('error');
            return Promise.reject(validateMessages.pattern.password.notFulfillingRegex)
        }
        setPasswordValidationStatus(undefined);
        return Promise.resolve()
    };

    const validatePasswordConfirm = (rule: any, value: string, callback: any) => {
        if (!withConfirmForm) return;
        const { password, passwordConfirm } = withConfirmForm.getFieldsValue();
        if (!passwordsMatch(password, passwordConfirm)) {
            setPasswordConfirmValidationStatus('error');
            return Promise.reject(validateMessages.pattern.password.notMatching);
        }
        if (!noRegexValidation && !value.match(passwordRegex)) {
            setPasswordConfirmValidationStatus('error');
            return Promise.reject(validateMessages.pattern.password.notFulfillingRegex);
        }
        setPasswordConfirmValidationStatus(undefined);
        return Promise.resolve();
    };

    return (
        <>
            <Form.Item
                label={customLabel ?? "Passwort"}
                name="password"
                validateStatus={passwordValidationStatus}
                rules={[{ required: true, validator: validatePassword }]}>
                <Input.Password
                    disabled={disabled}
                />
            </Form.Item>
            {withConfirmForm &&
                <Form.Item
                    label={`${customLabel ?? "Passwort"} wiederholen`}
                    name="passwordConfirm"
                    validateStatus={passwordValidationStatus}
                    rules={[{ required: true, validator: validatePasswordConfirm }]}>
                    <Input.Password
                        disabled={disabled}
                    />
                </Form.Item>
            }
        </>
    );
};

export default PasswordInput;