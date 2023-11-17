flatpickr('.date-form', {
  locale: 'ja',
  minDate: new Date(2010, 1, 0),
  maxDate: new Date().setMonth(new Date().getMonth() + 12),
});