import { Layout } from 'antd';
import { Header, Content } from 'antd/lib/layout/layout';
import { Outlet } from 'react-router-dom';

import './App.css';
import GlobalNav from './components/GlobalNav/GlobalNav';

function App() {
  return (
    <Layout style={{ backgroundColor: '#D8D8D8' }}>
      <Header>
        <GlobalNav />
      </Header>
      <Layout>
        <Content>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}

export default App;
