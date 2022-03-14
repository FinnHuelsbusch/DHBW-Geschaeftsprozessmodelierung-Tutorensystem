import { Form, Input } from 'antd';
import React from 'react';
import { validateMessages } from '../../utils/Messages';

type Props = {
    disabled?: boolean,
    required?: boolean,
    onChange?: (value: string) => void
}

const mailPatternStudent = /^s[0-9]{6}@student\.dhbw-mannheim\.de$/g;
const mailPatternOthers = /^[a-z]*\.[a-z]*@dhbw-mannheim\.de$/g;

export const isValidEmail = (email: string): boolean => {
    email = email.trim().toLowerCase();
    return !!(email.match(mailPatternStudent) || email.match(mailPatternOthers))
}

export const isDirectorEmail = (email: string): boolean => {
    email = email.trim().toLowerCase();
    return !!email.match(mailPatternOthers);
}

const EmailFormInput: React.FC<Props> = ({ disabled = false, required = false, onChange = undefined }) => {

    const validate = (rule: any, value: string, callback: any) => {
        if (!value || value === "") {
            callback(validateMessages.required);
            return;
        }
        value = value.trim().toLowerCase();
        if (isValidEmail(value)) {
            callback();
        } else {
            callback(validateMessages.pattern.invalid);
        }
    };

    const style = { color: disabled ? 'gray' : 'initial' };

    return (
        <Form.Item
            label="E-Mail"
            name="email"
            tooltip="Nur Mailadressen der DHBW sind zugelassen"
            rules={[{
                required: required,
                validator: validate
            }]}>
            <Input
                // key="mymail123"
                disabled={disabled}
                onChange={onChange ? (e) => onChange(e.target.value) : undefined}
                style={style}
            />
        </Form.Item>
    );
}

export default EmailFormInput;