<g:datePicker name="${property}" value="${value ?: new Date()}"
              precision="${attrs.precision}"
              cssClasses="[year: 'form-control custom-select col-2', 'month': 'form-control custom-select col-2', 'day': 'form-control custom-select col-2', 'hour': 'form-control custom-select col-1', 'minute': 'form-control custom-select col-1']"
              noSelection="['':'-Choose-']"/>