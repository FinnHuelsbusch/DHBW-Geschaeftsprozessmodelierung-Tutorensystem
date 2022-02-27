import { Button, message } from 'antd';
import React from 'react';
import { ping } from '../../api/api';

const Overview: React.FC = () => {
  return (
    <div>
      <Button onClick={e => {
        ping().then(
          res => message.success(res),
          err => message.error("Axios error")
        );
      }}>
        Ping backend
      </Button>
    </div>

  )
};

export default Overview;