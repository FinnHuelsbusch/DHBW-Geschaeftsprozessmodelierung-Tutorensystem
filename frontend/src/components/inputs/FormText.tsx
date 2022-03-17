import { Form, Input } from 'antd';
import React from 'react';

type Props = {
    children: React.ReactNode,
    [x: string]: any, // varargs
}

const FormText: React.FC<Props> = ({ children, ...varargs }) => {

    // force whitespace
    const rules = [{ whitespace: true }];
    if (varargs.rules) {
        rules.push(...varargs.rules);
    }

    return (
        <Form.Item
            {...varargs}
            rules={rules}
        >
            {children}
        </Form.Item>
    );
}

export default FormText;