import { Form, Input } from 'antd';
import { ValidateStatus } from 'antd/lib/form/FormItem';
import React from 'react';
import { validateMessages } from '../../utils/Messages';

export type PasswordFieldProps = {
    disabled?: boolean,
    validateStatus?: ValidateStatus,
    message?: string,
    customLabel?: string,
    validator?: (rule: any, value: string, callback: any) => void
}

declare function Password(props: PasswordFieldProps): React.ReactElement;
declare function PasswordConfirm(props: PasswordFieldProps): React.ReactElement;

interface IPasswordWithConfirm {
    Password: typeof Password,
    PasswordConfirm: typeof PasswordConfirm,
    passwordsMatch: (password: string, passwordConfirm: string) => boolean,
    mandatoryValidator: (rule: any, value: string, callback: any) => void,
    passwordConstraintValidator: (rule: any, value: string, callback: any) => void,
}

const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;

export const PasswordWithConfirm: IPasswordWithConfirm = {
    Password: (props: PasswordFieldProps) =>
        <Form.Item
            label={props.customLabel ?? "Passwort"}
            name="password"
            validateStatus={props.validateStatus}
            help={props.message}
            rules={[{ required: true, validator: props.validator }]}>
            <Input.Password
                disabled={props.disabled}
            />
        </Form.Item>,
    PasswordConfirm: (props: PasswordFieldProps) =>
        <Form.Item
            label={props.customLabel ?? "Passwort wiederholen"}
            name="passwordConfirm"
            validateStatus={props.validateStatus}
            help={props.message}
            rules={[{ required: true, validator: props.validator }]}>
            <Input.Password
                disabled={props.disabled}
            />
        </Form.Item>,
    passwordsMatch: (password: string, passwordConfirm: string): boolean => {
        return (
            password != null && password.trim() !== ""
            && passwordConfirm === password
        );
    },
    mandatoryValidator: (rule: any, value: string, callback: any) => {
        if (!value || value === "") {
            callback(validateMessages.required);
        } else {
            callback();
        }
    },
    passwordConstraintValidator: (rule: any, value: string, callback: any) => {
        if (!value || value === "") {
            callback(validateMessages.required);
            return;
        }
        if (value.match(passwordRegex)) {
            callback();
        } else {
            callback(validateMessages.pattern.password);
        }
    },
}