import {SketchField, Tools} from 'react-sketch';
import {PageHeader, Panel, Grid, Row, Col, Button, ButtonGroup, ButtonToolbar, Label} from 'react-bootstrap';
import {Creatable} from 'react-select';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

var getImage = function () {
    var c = document.querySelector(".lower-canvas")
    return c.toDataURL();
}

class App extends React.Component {

    constructor(params) {
        super(params);

        this.onClear = this.onClear.bind(this);
        this.onPredict = this.onPredict.bind(this);
        this.state = {answer: null};
        client({method: 'GET', path: '/api/labels'}).then(response => {
            this.setState({labels: response.entity});
        });
    }

    onPredict() {
        var imgData = getImage();
        client({method: 'POST', path: '/api/predict', entity: imgData}).then(response => {
            this.setState({answer: response.entity});
        });
    }

    onClear() {
        this.sketch.clear();
        this.setState({answer: null});
    }

    render() {
        return (
            <Grid>
                <Row className="show-grid">
                    <Col xsHidden md={2}/>
                    <Col xs={12} md={8}>
                        <PageHeader>Teach me: <small>Ask a question</small>
                        </PageHeader>
                    </Col>
                    <Col xsHidden md={2}/>
                </Row>
                <Row className="show-grid">
                    <Col xsHidden md={2}/>
                    <Col xs={8} md={5}>
                        <Panel>
                            <SketchField
                                height='300px'
                                ref={(c) => this.sketch = c}
                                tool={Tools.Pencil}
                                color='black'
                                lineWidth={3}/>
                        </Panel>
                        <Button bsStyle="default" onClick={this.onClear}>Clear</Button>
                    </Col>
                    <Col xs={4} md={3}>
                        {this.state.answer
                            ? <Answer parent={this} options={this.state.labels} value={this.state.answer}/>
                            : <Prompt parent={this} labels={this.state.labels}/>}
                    </Col>
                    <Col xsHidden md={2}/>
                </Row>
            </Grid>
        )
    }
}

class FitSelector extends React.Component {
    constructor(params) {
        super(params);
        this.onFit = this.onFit.bind(this);
        this.state = {label: null};
    }

    onFit() {
        let imgData = getImage();
        client({method: 'POST', path: '/api/fit?label=' + this.state.label.value, entity: imgData});
        this.props.app.onClear();
    }

    render() {
        return (
            <div>
                <Creatable
                    name="form-field-name"
                    value={this.state.label}
                    onChange={value => this.setState({label: value})}
                    options={this.props.options}/>
                <h3>
                    <ButtonToolbar>
                        <Button bsStyle="primary" onClick={this.onFit}>Submit</Button>
                        <Button onClick={this.props.app.onClear}>Nevermind</Button>
                    </ButtonToolbar>
                </h3>
            </div>
        );
    }
}

class Prompt extends React.Component {
    render() {
        let labs = this.props.labels
            ? this.props.labels.map(c => <li key={c.value}>{c.label}</li>)
            : "";
        return (
            <div>
                <h3>{'Draw a simple shape and I will try to guess what it is. I know about: '}</h3>
                <h4>
                    <ul>
                        {labs}
                    </ul>
                </h4>
                <h3><Button bsStyle="primary" bsSize="large" onClick={this.props.parent.onPredict}>What is it?</Button></h3>
            </div>
        );
    }
}

class Answer extends React.Component {

    constructor(params) {
        super(params);
        this.state = {sure: this.props.value.confidence === 'SURE', label: null};
    }
    render() {
        let selector = this.getSelector();

        let title = this.getTitle();

        return (<div>{title}{selector}</div>)
    }

    getTitle() {
        let confidence = this.props.value.confidence;
        if (confidence === 'SURE') {
            return (
                <div>
                    <h3>This is</h3>
                    <h3>
                        <Label bsStyle="primary">{this.props.value.labels[0]}</Label>
                    </h3>
                </div>
            );
        } else if (confidence === 'UNCERTAIN') {
            return (
                <div>
                    <h3>I am not sure. It is either</h3>
                    <h3>
                        <Label bsStyle="primary">{this.props.value.labels[0]}</Label> or <Label bsStyle="primary">{this.props.value.labels[1]}</Label>
                    </h3>
                </div>
            );
        } else {
            return (
                <div>
                    <h3>Oh! I have no idea what it is. Help me, please.</h3>
                </div>
            );
        }
    }

    getSelector() {
        if (this.state.sure) {
            return (
                <div>
                    <h3>I'm sure, right?</h3>
                    <ButtonGroup>
                        <Button bsStyle="primary" onClick={this.props.parent.onClear}>Yes</Button>
                        <Button bsStyle="default" onClick={() => this.setState({sure: false})}>No</Button>
                    </ButtonGroup>
                </div>
            );
        } else {
            return (
                <div>
                    <h3>Tell me what it was</h3>
                    <FitSelector options={this.props.options} app={this.props.parent}/>
                </div>
            );
        }
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('react')
);
