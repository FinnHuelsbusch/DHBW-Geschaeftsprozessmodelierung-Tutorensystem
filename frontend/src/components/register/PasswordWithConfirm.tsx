import { Form, Input } from 'antd';
import { ValidateStatus } from 'antd/lib/form/FormItem';
import React from 'react';

export type PasswordFieldProps = {
    disabled?: boolean,
    validateStatus: ValidateStatus | undefined,
    message: string | undefined,
    customLable?: string
}

declare function Password(props: PasswordFieldProps): React.ReactElement;
declare function PasswordConfirm(props: PasswordFieldProps): React.ReactElement;

interface IPasswordWithConfirm {
    Password: typeof Password,
    PasswordConfirm: typeof PasswordConfirm,
    passwordsMatch: (password: string, passwordConfirm: string) => boolean
}

const PasswordWithConfirm: IPasswordWithConfirm = {
    Password: (props: PasswordFieldProps) =>
        <Form.Item
            label={props.customLable ?? "Passwort"}
            name="password"
            validateStatus={props.validateStatus}
            help={props.message}
            rules={[{ required: true }]}>
            <Input.Password
                disabled={props.disabled}
            />
        </Form.Item>,
    PasswordConfirm: (props: PasswordFieldProps) =>
        <Form.Item
            label={props.customLable ?? "Passwort wiederholen"}
            name="passwordConfirm"
            validateStatus={props.validateStatus}
            help={props.message}
            rules={[{ required: true }]}>
            <Input.Password
                disabled={props.disabled}
            />
        </Form.Item>,
    passwordsMatch: (password: string, passwordConfirm: string): boolean => {
        return (
            password != null && password.trim() !== ""
            && passwordConfirm != null && passwordConfirm === password
        );
    }
}

export default PasswordWithConfirm;