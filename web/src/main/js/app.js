import {SketchField, Tools} from 'react-sketch';
import {PageHeader,Panel,Grid,Row,Col,Button,ButtonGroup,Label,Modal} from 'react-bootstrap';
import {Creatable} from 'react-select';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

var getImage = function() {
    var c = document.querySelector(".lower-canvas")
    return c.toDataURL();
}

class App extends React.Component {

    constructor(params) {
        super(params);

        this.onClear = this.onClear.bind(this);
        this.onEvaluate = this.onEvaluate.bind(this);
        this.state = { answer: null };
        client({method: 'GET', path: '/api/categories'}).then(response => {
            this.setState({ categories: response.entity });
        });
    }

    onEvaluate() {
      var imgData = getImage();
      client({method: 'POST', path: '/api/evaluate', entity: imgData}).then(response => {
        this.setState({ answer: response.entity });
      });
    }

    onClear() {
      this.sketch.clear();
      this.setState({ answer: null });
    }

    render() {
      return (
              <Grid>
                <Row className="show-grid">
                  <Col xsHidden md={3}></Col>
                  <Col xs={12} md={6}>
                    <PageHeader>Teach me: <small>Ask a question</small></PageHeader>
                  </Col>
                  <Col xsHidden md={3}></Col>
                </Row>
                <Row className="show-grid">
                  <Col xsHidden md={3}></Col>
                  <Col xs={8} md={4}>
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
                  <Col xs={4} md={2}>
                    { this.state.answer
                      ? <Answer parent={this} options={this.state.categories} value={this.state.answer} />
                      : <Prompt parent={this} categories={this.state.categories}/>}
                  </Col>
                  <Col xsHidden md={3}></Col>
                </Row>
              </Grid>
            )
      }
}

class Prompt extends React.Component {
    render() {
         var cats = this.props.categories
                        ? this.props.categories.map(c => <li key={c.value}>{c.label}</li>)
                        : "";
          return (
            <div>
                <h3>{'Draw a simple shape and I will try to guess what it is. I know about: '}</h3>
                <h4>
                    <ul>
                        {cats}
                    </ul>
                </h4>
                <h3><Button bsStyle="primary" bsSize="large" onClick={this.props.parent.onEvaluate}>What is it?</Button></h3>
            </div>
    );}
}

class Answer extends React.Component {

    constructor(params) {
      super(params);

      this.onYes = this.onYes.bind(this);
      this.onNo = this.onNo.bind(this);
      this.closeYes = this.closeYes.bind(this);
      this.closeNo = this.closeNo.bind(this);
      this.state = { showYes: false, showNo: false, category: null };
    }

    onYes() {
      this.setState({ showYes: true });
    }

    onNo() {
      this.setState({ showNo: true });
    }

    closeYes() {
      this.setState({ showYes: false });
    }

    closeNo() {
      this.setState({ showNo: false });
      var imgData = getImage();
      client({method: 'POST', path: '/api/train?category=' + this.state.category.value, entity: imgData});
    }

    render() {
      return (
        <div>
          <h2><Label bsStyle="primary">{this.props.value}</Label></h2>
          <h3>Am I right?</h3>
          <ButtonGroup>
            <Button bsStyle="primary" onClick={this.onYes}>Yes</Button>
            <Button bsStyle="default" onClick={this.onNo}>No</Button>
          </ButtonGroup>

          <Modal show={this.state.showYes} onHide={this.closeYes}>
            <Modal.Header closeButton>
              <Modal.Title>Hurrah!</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <h4>I know, I am awesome</h4>
            </Modal.Body>
            <Modal.Footer>
              <Button onClick={this.closeYes}>Close</Button>
            </Modal.Footer>
          </Modal>

          <Modal show={this.state.showNo} onHide={this.closeNo}>
            <Modal.Header closeButton>
              <Modal.Title>Oh no!</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <h4>Tell me what it was</h4>
              <Creatable
                        name="form-field-name"
                        value={this.state.category}
                        onChange={value => this.setState({ category: value })}
                        options={this.props.options}/>
            </Modal.Body>
            <Modal.Footer>
              <Button bsStyle="primary" onClick={this.closeNo}>Submit</Button>
            </Modal.Footer>
          </Modal>
        </div>
      )
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('react')
);
