ALTER TABLE public.appointments
DROP CONSTRAINT appointments_status_check;

ALTER TABLE public.appointments
    ADD CONSTRAINT appointments_status_check
        CHECK (
            status IN (
                       'PENDING',
                       'CONFIRMED',
                       'CHECKED_IN',
                       'DONE',
                       'NO_SHOW',
                       'CANCELLED'
                )
            );
