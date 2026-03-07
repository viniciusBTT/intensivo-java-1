document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('[data-confirm-delete]').forEach((form) => {
    form.addEventListener('submit', (event) => {
      event.preventDefault();

      const text = form.dataset.confirmText || 'Confirma a exclusão?';
      Swal.fire({
        title: 'Confirmar exclusão',
        text,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#b91c1c',
        cancelButtonColor: '#475569',
        confirmButtonText: 'Excluir',
        cancelButtonText: 'Cancelar',
      }).then((result) => {
        if (result.isConfirmed) {
          form.submit();
        }
      });
    });
  });
});
