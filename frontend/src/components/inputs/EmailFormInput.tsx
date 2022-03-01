import { Form } from 'antd';
import TextArea from 'antd/lib/input/TextArea';
import React from 'react';


const EmailFormInput: React.FC = () => {

    const mailPatternStudent = /^s[0-9]{6}@student\.dhbw-mannheim\.de$/g;
    const mailPatternOthers = /^[a-z]*\.[a-z]*@dhbw-mannheim\.de$/g;

    const validate = (rule: any, value: string, callback: any) => {
        if (!value || value === "") {
            callback("Pflichtfeld");
            return;
        }
        value = value.trim().toLowerCase();
        if (value.match(mailPatternStudent)
            || value.match(mailPatternOthers)) {
            callback();
        } else {
            callback("Ungültiges Format");
        }
    };

    return (
        <Form.Item
            label="E-Mail"
            name="email"
            rules={[{
                required: true,
                validator: validate
            }]}>
            <TextArea />
        </Form.Item>
    );
}

export default EmailFormInput;