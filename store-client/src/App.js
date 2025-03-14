import React from 'react';
import {BrowserRouter, Route} from 'react-router-dom';
import Notifications from 'react-notify-toast';
import Home from './components/main/Home';
import Login from './components/authentication/Login';
import Header from './components/main/Header';
import Cart from './components/products/Cart';
import Help from './components/main/Help.jsx';
import GameCatalogue from './components/products/ProductCatalogue';
import Register from './components/authentication/Register';
import Footer from './components/main/Footer';
import Contacts from './components/main/Contacts';
import UserProfile from './components/user/UserProfile';
import AddProduct from './components/products/AddProduct';
import AdminPanel from './components/admin/AdminPanel';
import GameSearch from "./components/products/ProductSearch";
import EditProduct from "./components/products/EditProduct";
import EditProfile from "./components/admin/EditProfile";
import ProductDetail from "./components/products/ProductDetail";


class App extends React.Component {
    render() {
        return (
            <BrowserRouter>
                <div style={{backgroundColor: "lightyellow"}}>
                    <Notifications options={{zIndex: 200, top: 20}}/>
                    <Header/>
                    <div style={{marginTop: 100, minHeight: 710}}>
                        <Route path="/" component={Home} exact/>
                        <Route path="/login" component={Login}/>
                        <Route path="/register" component={Register}/>
                        <Route path="/cart" component={Cart}/>
                        <Route path="/help" component={Help}/>
                        <Route path="/contacts" component={Contacts}/>
                        <Route path="/product" component={ProductDetail}/>
                        <Route path="/editProduct" component={EditProduct}/>
                        <Route path="/catalogue" component={GameCatalogue}/>
                        <Route path="/account" component={UserProfile}/>
                        <Route path="/editAccount" component={EditProfile}/>
                        <Route path="/addProduct" component={AddProduct}/>
                        <Route path="/adminPanel" component={AdminPanel}/>
                        <Route path="/search" component={GameSearch}/>
                    </div>
                </div>
                <Footer/>
            </BrowserRouter>
        );
    }
}

export default App;
