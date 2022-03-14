import { Form, Input } from 'antd';
import React from 'react';

type Props = {
    disabled?: boolean,
    required?: boolean
}

const mailPatternStudent = /^s[0-9]{6}@student\.dhbw-mannheim\.de$/g;
const mailPatternOthers = /^[a-z]*\.[a-z]*@dhbw-mannheim\.de$/g;

export const isValidEmail = (email: string): boolean => {
    email = email.trim().toLowerCase();
    return !!(email.match(mailPatternStudent) || email.match(mailPatternOthers))
}

const EmailFormInput: React.FC<Props> = ({ disabled = false, required = false }) => {

    const validate = (rule: any, value: string, callback: any) => {
        if (!value || value === "") {
            callback("Pflichtfeld");
            return;
        }
        value = value.trim().toLowerCase();
        if (isValidEmail(value)) {
            callback();
        } else {
            callback("Ungültiges Format");
        }
    };

    const style = { color: disabled ? 'gray' : 'initial' };

    return (
        <Form.Item
            label="E-Mail"
            name="email"
            rules={[{
                required: required,
                validator: validate
            }]}>
            <Input
                disabled={disabled}
                style={style}
            />
        </Form.Item>
    );
}

export default EmailFormInput;