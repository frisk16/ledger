flatpickr('.date-form', {
  locale: 'ja',
  minDate: new Date().setMonth(new Date().getMonth() - 12),
  maxDate: new Date().setMonth(new Date().getMonth() + 12),
});