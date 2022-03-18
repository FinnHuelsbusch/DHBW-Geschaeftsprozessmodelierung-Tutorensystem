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

type ValidationState = {
    validateStatus: ValidateStatus | undefined,
    message: string | undefined
}

const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/g;

const PasswordInput: React.FC<Props> = ({ disabled = false, customLabel, withConfirmForm, noRegexValidation = false }) => {

    const [passwordInfo, setPasswordInfo] = useState<ValidationState>({
        validateStatus: undefined,
        message: undefined
    });

    const [passwordConfirmInfo, setPasswordConfirmInfo] = useState<ValidationState>({
        validateStatus: undefined,
        message: undefined
    });

    const passwordsMatch = (password: string, passwordConfirm: string): boolean => {
        return (
            password != null && password.trim() !== ""
            && passwordConfirm === password
        );
    };

    const validatePassword = (rule: any, value: string, callback: any) => {
        if (!value || value === "") {
            setPasswordInfo({
                validateStatus: 'error',
                message: validateMessages.required
            });
            return;
        }
        if (!noRegexValidation && !value.match(passwordRegex)) {
            setPasswordInfo({
                validateStatus: 'error',
                message: validateMessages.pattern.password.notFulfillingRegex
            });
            return;
        }
        setPasswordInfo({
            validateStatus: undefined,
            message: undefined
        });
        callback();
    };

    const validatePasswordConfirm = (rule: any, value: string, callback: any) => {
        if (!withConfirmForm) return;
        const { password, passwordConfirm } = withConfirmForm.getFieldsValue();
        if (!passwordsMatch(password, passwordConfirm)) {
            setPasswordConfirmInfo({
                validateStatus: 'error',
                message: validateMessages.pattern.password.notMatching
            });
            return;
        }
        if (!noRegexValidation && !value.match(passwordRegex)) {
            setPasswordConfirmInfo({
                validateStatus: 'error',
                message: validateMessages.pattern.password.notFulfillingRegex
            });
            return;
        }
        setPasswordConfirmInfo({
            validateStatus: undefined,
            message: undefined
        });
        callback();
    };

    return (
        <>
            <Form.Item
                label={customLabel ?? "Passwort"}
                name="password"
                validateStatus={passwordInfo.validateStatus}
                help={passwordInfo.message}
                rules={[{ required: true, validator: validatePassword }]}>
                <Input.Password
                    disabled={disabled}
                />
            </Form.Item>
            {withConfirmForm &&
                <Form.Item
                    label={`${customLabel ?? "Passwort"} wiederholen`}
                    name="passwordConfirm"
                    validateStatus={passwordConfirmInfo.validateStatus}
                    help={passwordConfirmInfo.message}
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