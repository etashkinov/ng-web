const React = require('react');
const ReactDOM = require('react-dom');
const Rest = require('rest');
const LC = require('literallycanvas');


class App extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <SubmitForm/>
        )
    }
}

class SubmitForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {value: ''};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit() {
        Rest({method: 'POST', path: '/api/evaluate', entity: this.state.value}).done(response => {
            this.state = {value: response.entity};
        });
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    render() {

        return (
            <form onSubmit={this.handleSubmit}>
                <label>
                    Image:
                    <input type="file" onChange = {this.handleChange}  />
                </label>
                <input type="submit" value="Submit"/>
            </form>
        )
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('react')
);
