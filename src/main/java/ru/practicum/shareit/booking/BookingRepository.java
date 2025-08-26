package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBooker(User user, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(
            User user,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort
    );

    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now, Sort sort);

    List<Booking> findAllByStatusAndBooker(BookingStatus status, User user, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(
            Integer userId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort
    );

    List<Booking> findAllByItemOwnerAndEndBefore(Integer userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(Integer userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByStatusAndItemOwner(BookingStatus status, Integer userId, Sort sort);

    List<Booking> findAllByItemOwner(Integer userId, Sort sort);

    boolean existsByItemIdAndBookerIdAndEndBefore(Integer itemId, Integer bookerId, LocalDateTime now);

    List<Booking> findAllByItemId(Integer itemId);

    Booking findTopByItemIdAndEndAfterOrderByEndDesc(Integer itemId, LocalDateTime now);

    Booking findTopByItemIdAndStartAfterAndStatusOrderByStartAsc(Integer itemId,
                                                                 LocalDateTime now,
                                                                 BookingStatus status);

}
